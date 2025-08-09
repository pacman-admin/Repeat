package core.webui.server.handlers.internals.taskcreation;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class ActionEditSourceHandler extends AbstractSingleMethodHttpHandler {

    public ActionEditSourceHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws IOException {
        return HttpServerUtilities.prepareTextResponse(exchange, 501, "Not Implemented (yet)");
    }
/*
	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		byte[] content = HttpServerUtilities.getPostContent(request);
		if (content == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 500, "Failed to get POST content.");
		}

		String source = new String(content, StandardCharsets.UTF_8);
		backEndHolder.editSourceCode(source);
		return HttpServerUtilities.prepareTextResponse(exchange, 200, "");
	}*/
}
