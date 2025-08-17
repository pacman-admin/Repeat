package core.webui.server.handlers;

import core.webui.webcommon.HttpHandlerWithBackend;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public abstract class AbstractSingleMethodHttpHandler extends HttpHandlerWithBackend {

    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";

    private String allowedMethod;

    public AbstractSingleMethodHttpHandler(String allowedMethod) {
        this.allowedMethod = allowedMethod;
    }

    @Override
    protected final void handleWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
        if (allowedMethod != null && !request.getRequestLine().getMethod().equalsIgnoreCase(allowedMethod)) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Only " + allowedMethod + " requests are accepted.");
            return;
        }

        handleAllowedRequestWithBackend(request, exchange, context);
    }

    protected final Void emptySuccessResponse(HttpAsyncExchange exchange) throws IOException {
        return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
    }

    protected abstract Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException;
}