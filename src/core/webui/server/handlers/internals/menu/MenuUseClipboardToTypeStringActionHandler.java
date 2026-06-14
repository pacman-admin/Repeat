package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import frontEnd.Backend;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.simplenativehooks.utilities.Platform;

public final class MenuUseClipboardToTypeStringActionHandler extends AbstractBooleanConfigHttpHandler {
    @Override
    protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) {
        Backend.config.setUseClipboardToTypeString(value && Platform.isWindows());
        return emptySuccessResponse(exchange);
    }
}