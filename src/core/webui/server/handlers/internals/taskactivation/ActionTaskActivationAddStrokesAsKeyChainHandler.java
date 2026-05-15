package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;


import java.io.IOException;
import java.util.Map;

public final class ActionTaskActivationAddStrokesAsKeyChainHandler extends AbstractTaskActivationConstructorActionHandler {

    public ActionTaskActivationAddStrokesAsKeyChainHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    public void handleRequestWithBackendAndConstructor(HttpExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) {
        //LOGGER.fine("ActionTaskActivationAddStrokesAsKeyChainHandler");
        constructor.addAsKeyChain();
        constructor.stopListening();
        renderedTaskActivationPage(exchange, "fragments/key_chains", constructor);
    }
}