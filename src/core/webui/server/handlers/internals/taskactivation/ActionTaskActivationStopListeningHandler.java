package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.util.Map;

public class ActionTaskActivationStopListeningHandler extends AbstractTaskActivationConstructorActionHandler {

    public ActionTaskActivationStopListeningHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) {
        constructor.stopListening();
        constructor.clearStrokes();
        return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
    }
}