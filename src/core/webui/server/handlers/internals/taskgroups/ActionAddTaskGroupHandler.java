package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.Map;

import frontEnd.Backend;


import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public final class ActionAddTaskGroupHandler extends AbstractUIHttpHandler {

	public ActionAddTaskGroupHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange)
			throws IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
		if (params == null) {
			HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get parameters."); 
return;
		}

		String name = params.get("name");
		if (name == null || name.isBlank()) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "Group name must be provided."); 
return;
		}

		Backend.addTaskGroup(name);
		renderedTaskGroups(exchange);
	}
}
