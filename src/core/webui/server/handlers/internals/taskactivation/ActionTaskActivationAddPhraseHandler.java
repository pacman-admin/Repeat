package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;
import java.util.Map;

public final class ActionTaskActivationAddPhraseHandler extends AbstractTaskActivationConstructorActionHandler {

    public ActionTaskActivationAddPhraseHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) throws IOException {
        String phrase = params.get("phrase");
        if (phrase == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "No phrase provided.");
        }
        constructor.addPhrase(phrase);
        return renderedTaskActivationPage(exchange, "fragments/phrases", constructor);
    }
}