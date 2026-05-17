package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import frontEnd.Backend;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import utilities.OSIdentifier;

public final class MenuUseClipboardToTypeStringActionHandler extends AbstractBooleanConfigHttpHandler {
    @Override
    protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) {
        Backend.config.setUseClipboardToTypeString(value && OSIdentifier.getCurrentOS().isClipboardSupported);
        return emptySuccessResponse(exchange);
    }
}