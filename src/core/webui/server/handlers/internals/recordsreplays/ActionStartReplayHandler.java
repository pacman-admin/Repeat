package core.webui.server.handlers.internals.recordsreplays;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class ActionStartReplayHandler extends AbstractSingleMethodHttpHandler {

	public ActionStartReplayHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
		backEndHolder.startReplay();
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
