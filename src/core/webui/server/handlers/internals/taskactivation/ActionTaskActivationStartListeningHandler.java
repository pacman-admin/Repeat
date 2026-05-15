package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;


import java.util.Map;

public final class ActionTaskActivationStartListeningHandler extends AbstractTaskActivationConstructorActionHandler {

    public ActionTaskActivationStartListeningHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    public void handleRequestWithBackendAndConstructor(HttpExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) {
        constructor.startListening();
        HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
    }
}