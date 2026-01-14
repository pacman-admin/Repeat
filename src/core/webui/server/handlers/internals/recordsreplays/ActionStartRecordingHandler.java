package core.webui.server.handlers.internals.recordsreplays;

import main.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public final class ActionStartRecordingHandler extends AbstractSingleMethodHttpHandler {

	public ActionStartRecordingHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
		Backend.startRecording();
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
