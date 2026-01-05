package core.webui.webcommon;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Returns response saying that server is up and running.
 */
public class UpAndRunningHandler implements HttpAsyncRequestHandler<HttpRequest> {

    private static final Logger LOGGER = Logger.getLogger(UpAndRunningHandler.class.getName());

    @Override
    public void handle(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) {
        String msg = "Server is up and running.";
        LOGGER.info(msg);
        HttpServerUtilities.prepareTextResponse(exchange, 200, msg);
    }

    @Override
    public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest arg0, HttpContext arg1) {
        // Buffer request content in memory for simplicity.
        return new BasicAsyncRequestConsumer();
    }
}
