package core.webui.server.handlers.internals.menu;

import main.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public final class MenuHaltAllTasksActionHandler extends AbstractSingleMethodHttpHandler {

	public MenuHaltAllTasksActionHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
		Backend.haltAllTasks();
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
