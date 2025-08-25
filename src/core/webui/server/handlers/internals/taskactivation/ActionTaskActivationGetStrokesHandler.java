package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;
import java.util.Map;

public class ActionTaskActivationGetStrokesHandler extends AbstractTaskActivationConstructorActionHandler {
    public ActionTaskActivationGetStrokesHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) throws IOException {
        String strokes = constructor.getStrokes();
        return HttpServerUtilities.prepareHttpResponse(exchange, 200, strokes.isEmpty() ? "None" : strokes);
    }
}