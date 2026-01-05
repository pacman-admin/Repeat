package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class MenuCleanUnusedSourcesActionHandler extends AbstractSingleMethodHttpHandler {

    public MenuCleanUnusedSourcesActionHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) {
        backEndHolder.cleanUnusedSource();
        return emptySuccessResponse(exchange);
    }
}