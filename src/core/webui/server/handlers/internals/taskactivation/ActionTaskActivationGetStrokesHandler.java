package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;


import java.util.Map;

public final class ActionTaskActivationGetStrokesHandler extends AbstractTaskActivationConstructorActionHandler {
    public ActionTaskActivationGetStrokesHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    public void handleRequestWithBackendAndConstructor(HttpExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) {
        String strokes = constructor.getStrokes();
        HttpServerUtilities.prepareHttpResponse(exchange, 200, strokes.isBlank() ? "None" : strokes); 
return;
    }
}