package core.webui.server.handlers.internals.taskcreation;

import core.languageHandler.sourceGenerator.AbstractSourceGenerator;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import main.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public final class GetSourceTemplateHandler extends AbstractSingleMethodHttpHandler {

    public GetSourceTemplateHandler() {
        super(AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
        String source = AbstractSourceGenerator.getReferenceSource(Backend.getSelectedLanguage());
        return HttpServerUtilities.prepareTextResponse(exchange, 200, source);
    }
}
