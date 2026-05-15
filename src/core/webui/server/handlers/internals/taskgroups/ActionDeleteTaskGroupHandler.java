package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.Map;

import frontEnd.Backend;



import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractUIHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.CommonTask;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public final class ActionDeleteTaskGroupHandler extends AbstractUIHttpHandler {

	public ActionDeleteTaskGroupHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange)
			throws IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
		if (params == null) {
			HttpServerUtilities.prepareTextResponse(exchange, 400, "Failed to parse POST data."); 
return;
		}

		String id = CommonTask.getTaskGroupIdFromRequest( params);
		if (id == null || id.isBlank()) {
			HttpServerUtilities.prepareTextResponse(exchange, 400, "Cannot find task group from request data."); 
return;
		}

		Backend.removeTaskGroup(id);
		renderedTaskGroups(exchange);
	}
}
