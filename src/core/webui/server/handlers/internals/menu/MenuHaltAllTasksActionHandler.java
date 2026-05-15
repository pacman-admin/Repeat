package core.webui.server.handlers.internals.menu;

import frontEnd.Backend;


import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public final class MenuHaltAllTasksActionHandler extends AbstractSingleMethodHttpHandler {

	public MenuHaltAllTasksActionHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange) {
		Backend.haltAllTasks();
		HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
    }
}
