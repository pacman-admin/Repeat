package core.webui.server.handlers.internals.menu;

import java.io.IOException;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class MenuExitActionHandler extends AbstractSingleMethodHttpHandler {

	private static final long EXIT_DELAY_MS = 2000;

	public MenuExitActionHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws IOException {
		backEndHolder.scheduleExit(EXIT_DELAY_MS);
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "Exiting after " + EXIT_DELAY_MS + "ms...");
	}
}
