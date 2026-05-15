package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.MouseKey;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;


import java.awt.event.InputEvent;
import java.util.Map;

public final class ActionTaskActivationAddMouseKey extends AbstractTaskActivationConstructorActionHandler {

    public ActionTaskActivationAddMouseKey(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    public void handleRequestWithBackendAndConstructor(HttpExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) {
        if (!constructor.isListening()) {
            throw new IllegalStateException("Enable key listening before adding mouse click.");
        }
        if (!params.containsKey("key")) {
            throw new IllegalArgumentException("Request missing the 'key' parameter.");
        }
        int mouseKey;
        switch (params.get("key")) {
            case "LEFT" -> mouseKey = InputEvent.BUTTON1_DOWN_MASK;
            case "RIGHT" -> mouseKey = InputEvent.BUTTON3_DOWN_MASK;
            case "MIDDLE" -> mouseKey = InputEvent.BUTTON2_DOWN_MASK;
            default -> throw new IllegalArgumentException("Invalid key in exchange.");
        }
        constructor.addMouseKey(MouseKey.of(mouseKey));
        HttpServerUtilities.prepareHttpResponse(exchange, 200, constructor.getStrokes());
    }
}