package core.webui.server.handlers.internals.taskcreation;

import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;



public final class ActionStopRunningCompiledTaskHandler extends AbstractSingleMethodHttpHandler {

    public ActionStopRunningCompiledTaskHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        Backend.stopRunningCompiledAction();
        HttpServerUtilities.prepareTextResponse(exchange, 200, ""); 
return;
    }
}