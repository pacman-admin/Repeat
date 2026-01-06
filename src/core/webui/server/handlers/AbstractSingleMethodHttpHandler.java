package core.webui.server.handlers;

import core.webui.webcommon.HttpHandlerWithBackend;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;

public abstract class AbstractSingleMethodHttpHandler extends HttpHandlerWithBackend {

    protected static final String GET_METHOD = "GET";
    protected static final String POST_METHOD = "POST";
    private final String allowedMethod;

    public AbstractSingleMethodHttpHandler(String allowedMethod) {
        this.allowedMethod = allowedMethod;
    }

    @Override
    protected final void handleWithBackend(HttpRequest request, HttpAsyncExchange exchange) throws IOException {
        if (allowedMethod != null && !request.getRequestLine().getMethod().equalsIgnoreCase(allowedMethod)) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Only " + allowedMethod + " requests are accepted.");
            return;
        }

        handleAllowedRequestWithBackend(request, exchange);
    }

    protected final Void emptySuccessResponse(HttpAsyncExchange exchange){
        return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
    }

    protected abstract Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) throws IOException;
}