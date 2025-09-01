package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import utilities.OSIdentifier;

import java.io.IOException;

public class MenuUseClipboardToTypeStringActionHandler extends AbstractBooleanConfigHttpHandler {
    @Override
    protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) throws IOException {
        if (value && OSIdentifier.getCurrentOS().isClipboardSupported) {
            backEndHolder.getConfig().setUseClipboardToTypeString(value);
            return emptySuccessResponse(exchange);
        }
        backEndHolder.getConfig().setUseClipboardToTypeString(false);
        return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Using clipboard to type Strings is not supported on the current platform.");
    }
}