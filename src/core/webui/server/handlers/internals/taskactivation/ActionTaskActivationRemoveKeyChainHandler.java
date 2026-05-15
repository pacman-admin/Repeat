package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

import utilities.NumberUtility;

import java.io.IOException;
import java.util.Map;

public final class ActionTaskActivationRemoveKeyChainHandler extends AbstractTaskActivationConstructorActionHandler {

    public ActionTaskActivationRemoveKeyChainHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    public void handleRequestWithBackendAndConstructor(HttpExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) throws IOException {
        String index = params.get("index");
        if (!NumberUtility.isNonNegativeInteger(index)) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Index must be non-negative integer."); 
return;
        }

        constructor.removeKeyChain(Integer.parseInt(index));
        renderedTaskActivationPage(exchange, "fragments/key_chains", constructor);
    }
}
