package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;


import java.io.IOException;
import java.util.Map;

public final class ActionTaskActivationAddStrokesAsKeySequenceHandler extends AbstractTaskActivationConstructorActionHandler {

    public ActionTaskActivationAddStrokesAsKeySequenceHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    public void handleRequestWithBackendAndConstructor(HttpExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) {
        constructor.addAsKeySequence();
        constructor.stopListening();
        renderedTaskActivationPage(exchange, "fragments/key_sequences", constructor);
    }
}