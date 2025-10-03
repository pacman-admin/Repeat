package core.ipc.repeatServer.processors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.ipc.ApiProtocol;
import core.ipc.repeatServer.MainMessageSender;
import core.keyChain.ActionInvoker;
import frontEnd.MainBackEndHolder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * This class represents the message processor for all task action.
 * <p>
 * This module can initiate certain activities on the client:
 * 1) create task:
 * {
 * "task_action": "create_task",
 * "parameters" : [absolute path to the source file as string]
 * }
 * <p>
 * 2) remove task:
 * {
 * "task_action": "remove_task",
 * "parameters" : [task id as integer]
 * }
 * <p>
 * 3) run task
 * {
 * "task_action": "run_task",
 * "parameters": [task id as integer]
 * }
 * <p>
 * All these activities will be initiated by sending a message to client, and
 * wait on the replying message with the following JSON format
 * {
 * "status" : status of the action on client side,
 * "message" : information/debug message
 * }
 *
 * @author HP Truong
 *
 */
public class TaskProcessor extends AbstractMessageProcessor {

    public static final String CREATE_TASK_ACTION = "create_task";
    public static final String RUN_TASK_ACTION = "run_task";
    public static final String REMOVE_TASK_ACTION = "remove_task";
    public static final Charset SOURCE_ENCODING = StandardCharsets.UTF_8;
    private static final long TASK_CREATION_TIMEOUT_MS = 10000; // Compiling may take long time
    private static final long EXECUTION_TIMEOUT_MS = 500000; // Execution may also take long time
    private static final long TASK_REMOVAL_TIMEOUT_MS = 2000; // Removal should be fast
    private final Map<Long, Reply> locks;
    private final ServerTaskRequestProcessor taskRequestProcessor;

    public TaskProcessor(MainBackEndHolder backEnd, MainMessageSender messageSender) {
        super(messageSender);
        this.taskRequestProcessor = new ServerTaskRequestProcessor(backEnd, messageSender);
        locks = new HashMap<>();
    }

    @Override
    public boolean process(String type, long id, JsonNode content) throws InterruptedException {
        if (ApiProtocol.isReplyMessage(content)) {
            return processReply(type, id, content);
        }

        return taskRequestProcessor.process(type, id, content);
    }

    private boolean processReply(String type, long id, JsonNode content) {
        if (!locks.containsKey(id)) {
            getLogger().warning("Unknown id " + id + ". Drop message");
            return false;
        }
        if (!verifyReplyContent(content)) {
            getLogger().warning("Invalid reply." + content + ". Drop message");
            return false;
        }

        String status = content.getStringValue("status");
        JsonNode message = content.getNode("message");
        Reply output = locks.get(id);
        output.status = status;
        output.message = message;

        synchronized (output) {
            output.timeout = false;
            output.notify();
        }
        return true;
    }


    public boolean runTask(String id, ActionInvoker invoker) {
        JsonRootNode requestMessage = JsonNodeFactories.object(
                JsonNodeFactories.field("task_action", JsonNodeFactories.string(RUN_TASK_ACTION)),
                JsonNodeFactories.field("parameters",
                        JsonNodeFactories.array(
                                JsonNodeFactories.string(id),
                                invoker.jsonize()
                        )
                )
        );

        Reply reply = fullMessage(requestMessage, EXECUTION_TIMEOUT_MS);
        return reply != null && reply.status.equals(ApiProtocol.SUCCESS_STATUS);
    }


    private Reply fullMessage(JsonNode requestMessage, long timeoutMs) {
        if (!verifyMessageContent(requestMessage)) {
            getLogger().warning("Cannot send invalid message " + requestMessage);
            return null;
        }

        long messageId = messageSender.sendMessage(IpcMessageType.TASK.getValue(), requestMessage);
        if (messageId == -1) {
            return null;
        }

        Reply wait = new Reply();
        locks.put(messageId, wait);

        try {
            synchronized (wait) {
                wait.wait(timeoutMs);
            }
            if (wait.timeout) {
                getLogger().warning("Timeout on operation with id " + messageId);
                return null;
            } else {
                return wait;
            }
        } catch (InterruptedException e) {
            getLogger().log(Level.WARNING, "Interrupted while waiting for reply", e);
            return null;
        }
    }

    @Override
    protected boolean verifyMessageContent(JsonNode content) {
        return content.isStringValue("task_action") &&
                content.isArrayNode("parameters");
    }

    private static class Reply {
        private String status;
        private JsonNode message;
        private boolean timeout;

        private Reply() {
            timeout = true;
        }
    }
}
