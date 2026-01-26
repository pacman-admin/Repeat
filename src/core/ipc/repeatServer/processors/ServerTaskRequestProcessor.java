package core.ipc.repeatServer.processors;

import argo.jdom.JsonNode;
import core.ipc.repeatServer.ClientTask;
import core.ipc.repeatServer.MainMessageSender;
import core.keyChain.ActionInvoker;
import core.languageHandler.Language;
import core.languageHandler.compiler.Compiler;
import core.userDefinedTask.UserDefinedAction;
import frontEnd.Backend;
import utilities.FileUtility;
import utilities.json.JSONUtility;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static core.userDefinedTask.TaskGroupManager.COMPILER_FACTORY;

final class ServerTaskRequestProcessor extends AbstractMessageProcessor {

    private static final Logger LOGGER = Logger.getLogger(ServerTaskRequestProcessor.class.getName());

    public ServerTaskRequestProcessor(MainMessageSender messageSender) {
        super(messageSender);
    }

    @Override
    public boolean process(String type, long id, JsonNode content) {
        if (!verifyMessageContent(content)) {
            return false;
        }

        String action = content.getStringValue("task_action");
        JsonNode parameters = content.getNode("parameters");

        switch (action) {
            case TaskProcessor.CREATE_TASK_ACTION -> {
                return createServerTask(type, id, parameters);
            }
            case TaskProcessor.RUN_TASK_ACTION -> {
                return runServerTask(type, id, parameters);
            }
            case TaskProcessor.REMOVE_TASK_ACTION -> {
                return removeServerTask(type, id, parameters);
            }
        }

        getLogger().warning("Unknown request with action " + action + ".");
        return false;
    }

    private boolean createServerTask(String type, long id, JsonNode parameters) {
        String source;
        Language language = Language.JAVA;
        String previousId = "";

        if (parameters.isArrayNode()) {
            List<JsonNode> nodes = parameters.getArrayNode();
            if (nodes.size() != 1) {
                return failure(type, id, "Cannot create task with " + nodes.size() + " parameters.");
            }

            JsonNode sourceFileNode = nodes.getFirst();
            if (!sourceFileNode.isStringValue()) {
                return failure(type, id, "First parameter has to be source file path as a string.");
            }
            String sourceFilePath = sourceFileNode.getStringValue();
            File sourceFile = new File(sourceFilePath);
            if (!sourceFile.isFile() || !sourceFile.canRead()) {
                return failure(type, id, "Source file " + sourceFilePath + " is not a file or cannot be read.");
            }

            source = FileUtility.readFromFile(sourceFilePath).toString();
        } else if (parameters.isObjectNode()) {
            if (!parameters.isStringValue("language")) {
                return failure(type, id, "Missing compilation language.");
            }
            if (!parameters.isStringValue("source")) {
                return failure(type, id, "Missing source code.");
            }

            String languageValue = parameters.getStringValue("language");
            language = Language.identify(languageValue);
            if (language == null) {
                return failure(type, id, "Unknown language " + languageValue + ".");
            }

            if (parameters.isStringValue("previously_compiled_id")) {
                previousId = parameters.getStringValue("previously_compiled_id");
            }

            String encodedSource = parameters.getStringValue("source");
            byte[] base64Decoded = Base64.getDecoder().decode(encodedSource.getBytes());
            CharBuffer result = TaskProcessor.SOURCE_ENCODING.decode(ByteBuffer.wrap(base64Decoded));
            source = result.toString().trim();
        } else {
            return failure(type, id, "Unknown structure of parameters " + JSONUtility.jsonToString(parameters));
        }

        if (source.isBlank()) {
            return failure(type, id, "Source is empty or cannot extract source.");
        }

        UserDefinedAction existingAction = null;
        if (!previousId.isBlank()) {
            existingAction = Backend.getTask(previousId);
        }
        if (existingAction != null) {
            ClientTask response = ClientTask.of(existingAction.getActionId(), existingAction.getSourcePath());
            return success(type, id, response.jsonize());
        }
        UserDefinedAction newTask = createTask(source, language);
        if (newTask == null) {
            return failure(type, id, "Unable to compile source.");
        }

        ClientTask response = ClientTask.of(newTask.getActionId(), newTask.getSourcePath());
        return success(type, id, response.jsonize());
    }

    private UserDefinedAction createTask(String source, Language language) {
        Compiler compiler = COMPILER_FACTORY.getNativeCompiler(language);
        if (compiler == null) {
            LOGGER.warning("No compiler found for " + language + ".");
            return null;
        }
        UserDefinedAction action = Backend.compileSourceNatively(compiler, source, "remote-task");
        if (action == null) {
            LOGGER.warning("Compilation for remote task failed.");
            return null;
        }

        Backend.addRemoteCompiledTask(action);
        return action;
    }

    private boolean runServerTask(String type, long id, JsonNode parameters) {
        List<JsonNode> parameterNodes = parameters.getArrayNode();
        if (parameterNodes.isEmpty() || parameterNodes.size() > 2) {
            return failure(type, id, "Cannot run task with " + parameterNodes.size() + " parameters.");
        }

        String taskId = parameterNodes.getFirst().getStringValue();
        ActionInvoker activation = ActionInvoker.newBuilder().build();
        if (parameterNodes.size() == 2) {
            JsonNode activationNode = parameterNodes.get(1).getNode();
            activation = ActionInvoker.parseJSON(activationNode);
        }

        runTask(taskId, activation);
        return success(type, id);
    }

    private void runTask(String id, ActionInvoker actionInvoker) {
        UserDefinedAction action = Backend.getTask(id);
//        if (action == null) {
//            LOGGER.warning("No server action with ID " + id + " found.");
//            return;
//        }

        action.setInvoker(actionInvoker);
        try {
            action.trackedAction(Backend.getCore());
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while executing action.", e);
        }
    }

    private boolean removeServerTask(String type, long id, JsonNode parameters) {
        List<JsonNode> parameterNodes = parameters.getArrayNode();
        if (parameterNodes.size() != 1) {
            return failure(type, id, "Cannot remove task with " + parameterNodes.size() + " parameters.");
        }

        removeTask(parameterNodes.getFirst().getStringValue());
        return success(type, id);
    }

    private void removeTask(String id) {
        Backend.removeTask(id);
    }

    @Override
    protected boolean verifyMessageContent(JsonNode content) {
        return content.isStringValue("task_action") && content.isNode("parameters");
    }
}
