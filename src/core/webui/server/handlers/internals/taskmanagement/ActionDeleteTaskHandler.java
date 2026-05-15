package core.webui.server.handlers.internals.taskmanagement;

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

public final class ActionDeleteTaskHandler extends AbstractUIHttpHandler {

	public ActionDeleteTaskHandler(ObjectRenderer objectRenderer) {
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

		String taskId = CommonTask.getTaskIdFromRequest(params);
		if (taskId.isBlank()) {
			HttpServerUtilities.prepareTextResponse(exchange, 400, "Cannot find task from request data."); 
return;
		}

		Backend.removeCurrentTask(taskId);
		renderedTaskForGroup(exchange);
	}
}
