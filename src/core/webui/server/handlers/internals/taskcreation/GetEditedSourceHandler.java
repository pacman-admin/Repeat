package core.webui.server.handlers.internals.taskcreation;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class GetEditedSourceHandler extends AbstractSingleMethodHttpHandler {

    public GetEditedSourceHandler() {
        super(AbstractSingleMethodHttpHandler.GET_METHOD);
    }
    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws IOException {
        return HttpServerUtilities.prepareTextResponse(exchange, 503, "");
    }
}
