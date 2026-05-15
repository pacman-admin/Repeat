package core.webui.server.handlers.internals.recordsreplays;

import frontEnd.Backend;



import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public final class ActionStartReplayHandler extends AbstractSingleMethodHttpHandler {

	public ActionStartReplayHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange) {
		Backend.startReplay();
		HttpServerUtilities.prepareHttpResponse(exchange, 200, ""); 
return;
	}
}
