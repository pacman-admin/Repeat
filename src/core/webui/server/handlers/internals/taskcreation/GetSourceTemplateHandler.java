package core.webui.server.handlers.internals.taskcreation;

import core.languageHandler.sourceGenerator.AbstractSourceGenerator;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;

import java.util.logging.Logger;


public final class GetSourceTemplateHandler extends AbstractSingleMethodHttpHandler{

    public GetSourceTemplateHandler() {
        super(AbstractSingleMethodHttpHandler.GET_METHOD);
    }
    private static final Logger LOGGER = Logger.getLogger(GetSourceTemplateHandler.class.getName());
    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        String source = AbstractSourceGenerator.getReferenceSource(Backend.getSelectedLanguage());
        LOGGER.info(source);
        HttpServerUtilities.prepareTextResponse(exchange, 200, source);
    }
}
