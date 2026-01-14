package core.webui.webcommon;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public abstract class HttpHandlerWithBackend implements HttpAsyncRequestHandler<HttpRequest> {

    @Override
    public final void handle(HttpRequest request, HttpAsyncExchange exchange, HttpContext ignored)
            throws IOException {
        handle(request, exchange);
    }

    @Override
    public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest request, HttpContext ignored) {
        // Buffer request content in memory for simplicity.
        return new BasicAsyncRequestConsumer();
    }

    protected abstract void handle(HttpRequest request, HttpAsyncExchange exchange)
            throws IOException;
}
