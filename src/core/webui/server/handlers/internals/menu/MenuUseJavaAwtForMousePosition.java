package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import main.Main;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public final class MenuUseJavaAwtForMousePosition extends AbstractBooleanConfigHttpHandler {

    @Override
    protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) {
        Main.CONFIG.setUseJavaAwtToGetMousePosition(value);
        return emptySuccessResponse(exchange);
    }
}