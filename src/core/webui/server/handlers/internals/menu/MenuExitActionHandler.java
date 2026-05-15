package core.webui.server.handlers.internals.menu;

import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;


public final class MenuExitActionHandler extends AbstractSingleMethodHttpHandler {

    public MenuExitActionHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        Backend.exit(3000);
        HttpServerUtilities.prepareHttpResponse(exchange, 200, "Exiting...");
    }
}
