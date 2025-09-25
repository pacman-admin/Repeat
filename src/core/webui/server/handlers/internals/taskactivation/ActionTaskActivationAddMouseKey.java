package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.MouseKey;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.Map;

public class ActionTaskActivationAddMouseKey extends AbstractTaskActivationConstructorActionHandler {

    public ActionTaskActivationAddMouseKey(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) throws IOException {
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
            default -> throw new IllegalArgumentException("Invalid key in request.");
        }
        constructor.addMouseKey(MouseKey.of(mouseKey));
        return HttpServerUtilities.prepareHttpResponse(exchange, 200, constructor.getStrokes());
    }
}