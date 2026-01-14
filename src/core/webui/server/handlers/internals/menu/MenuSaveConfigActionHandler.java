package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import main.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public final class MenuSaveConfigActionHandler extends AbstractSingleMethodHttpHandler {

    public MenuSaveConfigActionHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
        Backend.writeConfigFile();
        return emptySuccessResponse(exchange);
    }
}