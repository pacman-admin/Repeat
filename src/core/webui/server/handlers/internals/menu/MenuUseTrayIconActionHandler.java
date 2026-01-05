package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public class MenuUseTrayIconActionHandler extends AbstractBooleanConfigHttpHandler {

    @Override
    protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) {
        return emptySuccessResponse(exchange);
    }
}