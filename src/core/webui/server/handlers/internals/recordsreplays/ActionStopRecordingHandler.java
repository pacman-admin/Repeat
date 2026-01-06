package core.webui.server.handlers.internals.recordsreplays;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class ActionStopRecordingHandler extends AbstractSingleMethodHttpHandler {

	public ActionStopRecordingHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
		backEndHolder.stopRecording();
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
