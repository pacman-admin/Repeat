package core.webui.webcommon;

import frontEnd.MainBackEndHolder;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class HttpHandlerWithBackend implements HttpAsyncRequestHandler<HttpRequest> {

    private static final Logger LOGGER = Logger.getLogger(HttpHandlerWithBackend.class.getName());

    protected MainBackEndHolder backEndHolder;

    public synchronized void setMainBackEndHolder(MainBackEndHolder backEndHolder) {
        this.backEndHolder = backEndHolder;
    }

    @Override
    public final void handle(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
            throws HttpException, IOException {
        if (backEndHolder == null) {
            LOGGER.warning("Missing backend");
            HttpServerUtilities.prepareTextResponse(exchange, 500, "Missing backend");
            return;
        }
        handleWithBackend(request, exchange, context);
    }

    @Override
    public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest request, HttpContext context) {
        // Buffer request content in memory for simplicity.
        return new BasicAsyncRequestConsumer();
    }

    protected abstract void handleWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
            throws IOException;
}
