package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.Map;

import frontEnd.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class ActionAddTaskGroupHandler extends AbstractUIHttpHandler {

	public ActionAddTaskGroupHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange)
			throws IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get parameters.");
		}

		String name = params.get("name");
		if (name == null || name.isBlank()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Group name must be provided.");
		}

		Backend.addTaskGroup(name);
		return renderedTaskGroups(exchange);
	}
}
