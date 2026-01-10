package core.webui.server.handlers.internals.taskcreation;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public final class ActionRunCompiledTaskHandler extends AbstractSingleMethodHttpHandler {

    public ActionRunCompiledTaskHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
        Backend.runCompiledAction();
        return HttpServerUtilities.prepareTextResponse(exchange, 200, "");
    }
}