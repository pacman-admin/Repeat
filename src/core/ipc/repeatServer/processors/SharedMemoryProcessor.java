package core.ipc.repeatServer.processors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.ipc.repeatServer.MainMessageSender;
import core.userDefinedTask.SharedVariables;
import utilities.Function;

import java.util.List;

/**
 * This class represents the message processor for any shared memory action.
 * <p>
 * A received message from the lower layer (central processor) will have the following JSON contents:
 * {
 * "device": fixed to be "shared_memory" (this may specify types of memory in the future),
 * "action" : a string specifying action,
 * "parameters" : a list of parameters for this action
 * }
 * <p>
 * ************************************************************************
 * The following actions are supported:
 * 1) get(string_namespace, string_varname): get value of a variable.
 * 2) set(string_namespace, string_varname, string_value): set value of a variable.
 * 3) del(string_namespace, string_varname): delete value of a variable.
 * 4) wait(string_namespace, string_varname, int_timeout_ms): wait for a variable value to be set.
 *
 * @author HP Truong
 */
public final class SharedMemoryProcessor extends AbstractMessageProcessor {

    private static final String DEVICE_NAME = "shared_memory";

    SharedMemoryProcessor(MainMessageSender messageSender) {
        super(messageSender);
    }

    @Override
    public boolean process(String type, long id, JsonNode content) throws InterruptedException {
        String action = content.getStringValue("action");
        List<JsonNode> parameterNodes = content.getArrayNode("parameters");
        List<String> params = new Function<JsonNode, String>() {
            @Override
            public String apply(JsonNode d) {
                return d.getStringValue();
            }
        }.map(parameterNodes);

        switch (action) {
            case "get" -> {
                return params.size() == 2 ? constructSuccessfulMessage(type, id, SharedVariables.getVar(params.get(0), params.get(1))) : failure(type, id, "Invalid parameter length " + params.size());
            }
            case "set" -> {
                return params.size() == 3 ? constructSuccessfulMessage(type, id, SharedVariables.setVar(params.get(0), params.get(1), params.get(2))) : failure(type, id, "Invalid parameter length " + params.size());
            }
            case "del" -> {
                return params.size() == 2 ? constructSuccessfulMessage(type, id, SharedVariables.delVar(params.get(0), params.get(1))) : failure(type, id, "Invalid parameter length " + params.size());
            }
            case "wait" -> {
                if (params.size() == 3) {
                    String timeoutMsString = params.get(2);
                    long timeoutMs;
                    try {
                        timeoutMs = Long.parseLong(timeoutMsString);
                    } catch (NumberFormatException e) {
                        return failure(type, id, "Third parameter must be integer " + timeoutMsString);
                    }

                    return constructSuccessfulMessage(type, id, SharedVariables.waitVar(params.get(0), params.get(1), timeoutMs));
                } else {
                    return failure(type, id, "Invalid parameter length " + params.size());
                }
            }
        }

        return failure(type, id, "Unknown action " + action);
    }

    private boolean constructSuccessfulMessage(String type, long id, String result) {
        return success(type, id,
                result != null ? JsonNodeFactories.string(result) : JsonNodeFactories.nullNode());
    }

    @Override
    protected boolean verifyMessageContent(JsonNode content) {
        return content.isStringValue("device") &&
                content.isStringValue("action") &&
                content.isArrayNode("parameters") &&
                content.getStringValue("device").equals(DEVICE_NAME);
    }
}
