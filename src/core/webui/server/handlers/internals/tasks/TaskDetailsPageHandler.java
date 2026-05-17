package core.webui.server.handlers.internals.tasks;

import com.sun.net.httpserver.HttpExchange;
import core.keyChain.ActionInvoker;
import core.keyChain.KeyChain;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedDetailedUserDefinedAction;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class TaskDetailsPageHandler extends AbstractUIHttpHandler {

    public static final String RECORD_TASK_NAME = "record";
    public static final String REPLAY_TASK_NAME = "replay";
    public static final String RUN_COMPILED_TASK_NAME = "runCompiled";
    public static final String MOUSE_GESTURE_ACTIVATION_TASK_NAME = "mouseGestureActivation";
    public static final Map<String, String> HOTKEY_NAMES;

    static {
        HOTKEY_NAMES = new HashMap<>();
        HOTKEY_NAMES.put(RECORD_TASK_NAME, "Start/Stop recording");
        HOTKEY_NAMES.put(REPLAY_TASK_NAME, "Start/Stop replaying");
        HOTKEY_NAMES.put(RUN_COMPILED_TASK_NAME, "Run compiled task");
        HOTKEY_NAMES.put(MOUSE_GESTURE_ACTIVATION_TASK_NAME, "Mouse gesture recognition activation/de-activation");
    }

    private final TaskActivationConstructorManager taskActivationConstructorManager;

    public TaskDetailsPageHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
        this.taskActivationConstructorManager = taskActivationConstructorManager;
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        Map<String, String> params = HttpServerUtilities.parseGetParameters(uri);

        String id = params.get("id");
        if (id == null || id.isBlank()) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Task ID is empty or not provided.");
            return;
        }
        if (isHotkey(id)) {
            handleNewHotkey(exchange, id);
        }

        UserDefinedAction action = Backend.getTask(id);

        String activationConstructorId = taskActivationConstructorManager.addNewConstructor(action.getActivation());
        TaskActivationConstructor activationConstructor = taskActivationConstructorManager.get(activationConstructorId);
        RenderedDetailedUserDefinedAction renderedDetailedUserDefinedAction = RenderedDetailedUserDefinedAction.fromUserDefinedAction(action, activationConstructor);
        renderTaskDetails(exchange, activationConstructorId, renderedDetailedUserDefinedAction);
    }

    private void handleNewHotkey(HttpExchange exchange, String taskString) throws IOException {
        String activationConstructorId = "";
        if (taskString.equals(RECORD_TASK_NAME)) {
            KeyChain recordKeyChain = Backend.config.getRECORD();
            activationConstructorId = taskActivationConstructorManager.addNewConstructor(ActionInvoker.newBuilder().withHotKey(recordKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false));
        }
        if (taskString.equals(REPLAY_TASK_NAME)) {
            KeyChain replayKeyChain = Backend.config.getREPLAY();
            activationConstructorId = taskActivationConstructorManager.addNewConstructor(ActionInvoker.newBuilder().withHotKey(replayKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false));
        }
        if (taskString.equals(RUN_COMPILED_TASK_NAME)) {
            KeyChain runCompiledKeyChain = Backend.config.getCOMPILED_REPLAY();
            activationConstructorId = taskActivationConstructorManager.addNewConstructor(ActionInvoker.newBuilder().withHotKey(runCompiledKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false));
        }
        if (taskString.equals(MOUSE_GESTURE_ACTIVATION_TASK_NAME)) {
            KeyChain mouseGestureKeyChain = Backend.config.getMOUSE_GESTURE();
            activationConstructorId = taskActivationConstructorManager.addNewConstructor(ActionInvoker.newBuilder().withHotKey(mouseGestureKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false).setMaxStrokes(1));
        }
        if (activationConstructorId.isBlank()) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Unknown hotkey " + taskString);
            return;
        }

        TaskActivationConstructor activationConstructor = taskActivationConstructorManager.get(activationConstructorId);
        RenderedDetailedUserDefinedAction renderedDetailedUserDefinedAction = RenderedDetailedUserDefinedAction.fromHotkey(taskString, HOTKEY_NAMES.getOrDefault(taskString, ""), activationConstructor);
        renderTaskDetails(exchange, activationConstructorId, renderedDetailedUserDefinedAction);
    }

    private void renderTaskDetails(HttpExchange exchange, String activationConstructorId, RenderedDetailedUserDefinedAction renderedDetailedUserDefinedAction) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("task", renderedDetailedUserDefinedAction);
        data.put("taskActivationConstructorId", activationConstructorId);
        renderedPage(exchange, "task_details", data);
    }

    private boolean isHotkey(String taskString) {
        return HOTKEY_NAMES.containsKey(taskString);
    }
}
