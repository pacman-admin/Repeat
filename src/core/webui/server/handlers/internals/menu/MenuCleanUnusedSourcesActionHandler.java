package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import frontEnd.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public final class MenuCleanUnusedSourcesActionHandler extends AbstractSingleMethodHttpHandler {

    public MenuCleanUnusedSourcesActionHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
        Backend.cleanUnusedSource();
        return emptySuccessResponse(exchange);
    }
}