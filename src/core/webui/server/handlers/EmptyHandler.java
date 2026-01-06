package core.webui.server.handlers;

import core.webui.webcommon.HttpHandlerWithBackend;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public class EmptyHandler extends HttpHandlerWithBackend {
    @Override
    protected void handleWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
        HttpServerUtilities.prepareHttpResponse(exchange, 200, "Feature disabled");
    }
}