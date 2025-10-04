package core.ipc.repeatServer.processors;

import core.ipc.repeatServer.MainMessageSender;
import frontEnd.MainBackEndHolder;
import utilities.ILoggable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class represents the central message processor.
 * There are four types of messages received from the client:
 * 1) action: See {@link core.ipc.repeatServer.processors.ControllerRequestProcessor}
 * 2) task: See {@link core.ipc.repeatServer.processors.TaskProcessor}
 * 3) shared_memory: See {@link core.ipc.repeatServer.processors.SharedMemoryProcessor}
 * 4) system_host: See {@link core.ipc.repeatServer.processors.SystemRequestProcessor}
 * 5) system_client: See {@link core.ipc.repeatServer.processors.SystemRequestProcessor}
 * <p>
 * A generic message received from client will have the following JSON format:
 * {
 * "type" : one of the four types above,
 * "id" : message id,
 * "content" : content to be processed by the upper layer
 * }
 * Note that it is essential for a message sent with id X be replied with message of the same id from client.
 * Conversely, a message received from client with id X should also be replied with the same id to client.
 *
 * @author HP Truong
 *
 */
public class ServerMainProcessor implements ILoggable {

    private final Map<IpcMessageType, AbstractMessageProcessor> messageProcessors;
    private final TaskProcessor taskProcessor;
    // Whether this processor is processing requests from local client.
    private boolean localClientProcessor;

    public ServerMainProcessor(MainBackEndHolder backEnd, MainMessageSender messageSender) {
        messageProcessors = new HashMap<>();

        ControllerRequestProcessor actionProcessor = new ControllerRequestProcessor(messageSender, backEnd.getCoreProvider(), this);
        taskProcessor = new TaskProcessor(backEnd, messageSender);
        SystemRequestProcessor systemProcessor = new SystemRequestProcessor(messageSender, this);
        SharedMemoryProcessor sharedMemoryProcessor = new SharedMemoryProcessor(messageSender);

        messageProcessors.put(IpcMessageType.ACTION, actionProcessor);
        messageProcessors.put(IpcMessageType.TASK, taskProcessor);
        messageProcessors.put(IpcMessageType.SHARED_MEMORY, sharedMemoryProcessor);
        messageProcessors.put(IpcMessageType.SYSTEM_HOST, systemProcessor);
        messageProcessors.put(IpcMessageType.SYSTEM_CLIENT, systemProcessor);
    }

    boolean isLocalClientProcessor() {
        return localClientProcessor;
    }

    void setLocalClientProcessor(boolean localClientProcessor) {
        this.localClientProcessor = localClientProcessor;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(ServerMainProcessor.class.getName());
    }

    public TaskProcessor getTaskProcessor() {
        return taskProcessor;
    }
}
