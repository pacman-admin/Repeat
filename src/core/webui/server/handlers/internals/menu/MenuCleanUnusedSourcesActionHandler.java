package core.webui.server.handlers.internals.menu;

import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import frontEnd.Backend;



public final class MenuCleanUnusedSourcesActionHandler extends AbstractSingleMethodHttpHandler {

    public MenuCleanUnusedSourcesActionHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        Backend.cleanUnusedSource();
        emptySuccessResponse(exchange);
    }
}