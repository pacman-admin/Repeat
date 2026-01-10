package core.webui.server.handlers.internals.taskmanagement;

import java.io.IOException;
import java.util.Map;

import frontEnd.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class ActionDeleteTaskHandler extends AbstractUIHttpHandler {

	public ActionDeleteTaskHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange)
			throws IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Failed to parse POST data.");
		}

		String taskId = CommonTask.getTaskIdFromRequest(params);
		if (taskId.isBlank()) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Cannot find task from request data.");
		}

		Backend.removeCurrentTask(taskId);
		return renderedTaskForGroup(exchange);
	}
}
