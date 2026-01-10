package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import frontEnd.Backend;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public class MenuExecuteOnReleaseActionHandler extends AbstractBooleanConfigHttpHandler {

    @Override
    protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) {
        Backend.config.setExecuteOnKeyReleased(value);
        return emptySuccessResponse(exchange);
    }
}
