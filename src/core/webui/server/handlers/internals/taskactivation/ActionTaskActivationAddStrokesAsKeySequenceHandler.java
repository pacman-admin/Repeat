package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;
import java.util.Map;

public class ActionTaskActivationAddStrokesAsKeySequenceHandler extends AbstractTaskActivationConstructorActionHandler {

    public ActionTaskActivationAddStrokesAsKeySequenceHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) throws IOException {
        constructor.addAsKeySequence();
        constructor.stopListening();
        return renderedTaskActivationPage(exchange, "fragments/key_sequences", constructor);
    }
}