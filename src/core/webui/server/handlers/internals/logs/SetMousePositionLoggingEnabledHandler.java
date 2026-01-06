package core.webui.server.handlers.internals.logs;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.util.Map;

public class SetMousePositionLoggingEnabledHandler extends AbstractSingleMethodHttpHandler {

    public SetMousePositionLoggingEnabledHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
        if (params == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
        }

        if (!params.containsKey("enabled")) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Missing required 'enabled' parameter.");
        }
        String enabledString = params.get("enabled");
        boolean enabled = enabledString.equals("" + true);

        backEndHolder.setEnabledMousePositionLogging(enabled);
        return emptySuccessResponse(exchange);
    }
}