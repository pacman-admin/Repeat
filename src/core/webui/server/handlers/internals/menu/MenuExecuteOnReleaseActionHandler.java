package core.webui.server.handlers.internals.menu;

import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import frontEnd.Backend;


public final class MenuExecuteOnReleaseActionHandler extends AbstractBooleanConfigHttpHandler {

    @Override
    public void handleAllowedRequestWithBackendAndValue(HttpExchange exchange, boolean value) {
        Backend.config.setExecuteOnKeyReleased(value);
        emptySuccessResponse(exchange);
return;
    }
}
