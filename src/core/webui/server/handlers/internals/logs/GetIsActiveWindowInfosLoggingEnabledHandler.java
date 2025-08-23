package core.webui.server.handlers.internals.logs;

import java.io.IOException;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class GetIsActiveWindowInfosLoggingEnabledHandler extends AbstractSingleMethodHttpHandler {

	public GetIsActiveWindowInfosLoggingEnabledHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws IOException {
		boolean response = backEndHolder.isActiveWindowInfosLoggingEnabled();
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "" + response);
	}
}
