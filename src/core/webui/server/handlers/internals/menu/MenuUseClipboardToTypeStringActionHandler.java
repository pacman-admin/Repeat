package core.webui.server.handlers.internals.menu;

import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import frontEnd.Backend;

import utilities.OSIdentifier;

public final class MenuUseClipboardToTypeStringActionHandler extends AbstractBooleanConfigHttpHandler {
    @Override
    public void handleAllowedRequestWithBackendAndValue(HttpExchange exchange, boolean value) {
        Backend.config.setUseClipboardToTypeString(value && OSIdentifier.getCurrentOS().isClipboardSupported);
        emptySuccessResponse(exchange);
return;
    }
}