package core.webui.server.handlers.internals.taskcreation;

import core.languageHandler.sourceGenerator.AbstractSourceGenerator;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;



public final class GetSourceTemplateHandler extends AbstractSingleMethodHttpHandler {

    public GetSourceTemplateHandler() {
        super(AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        String source = AbstractSourceGenerator.getReferenceSource(Backend.getSelectedLanguage());
        HttpServerUtilities.prepareTextResponse(exchange, 200, source); 
return;
    }
}
