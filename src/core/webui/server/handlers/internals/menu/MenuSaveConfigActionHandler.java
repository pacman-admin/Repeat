package core.webui.server.handlers.internals.menu;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import frontEnd.Backend;


public final class MenuSaveConfigActionHandler extends AbstractSingleMethodHttpHandler {

    public MenuSaveConfigActionHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        Backend.writeConfigFile();
        emptySuccessResponse(exchange);
        return;
    }
}