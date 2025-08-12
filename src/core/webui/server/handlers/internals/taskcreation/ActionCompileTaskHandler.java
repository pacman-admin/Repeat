package core.webui.server.handlers.internals.taskcreation;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class ActionCompileTaskHandler extends AbstractSingleMethodHttpHandler {

    private static final Logger LOGGER = Logger.getLogger(ActionCompileTaskHandler.class.getName());

    public ActionCompileTaskHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
        byte[] data = HttpServerUtilities.getPostContent(request);
        if (data == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Unable to get POST request data.");
        }

        String source = new String(data, StandardCharsets.UTF_8);
        LOGGER.info("POST data: " + source);
        boolean result = backEndHolder.compileSourceAndSetCurrent(source, null);
        if (!result) {
            LOGGER.warning("Unable to compile source code.");
            return HttpServerUtilities.prepareTextResponse(exchange, 500, "Could not compile source code!");
        }

        return HttpServerUtilities.prepareTextResponse(exchange, 200, "");
    }

}
