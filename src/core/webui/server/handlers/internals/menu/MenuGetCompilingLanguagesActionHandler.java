package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;



import java.io.IOException;

public final class MenuGetCompilingLanguagesActionHandler extends AbstractUIHttpHandler {

    public MenuGetCompilingLanguagesActionHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) throws IOException {
        renderedCompilingLanguages(exchange);
    }
}