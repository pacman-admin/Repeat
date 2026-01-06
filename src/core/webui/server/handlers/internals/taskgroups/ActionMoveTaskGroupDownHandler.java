package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

import static core.userDefinedTask.TaskGroupManager.moveTaskGroupDown;

public class ActionMoveTaskGroupDownHandler extends AbstractUIHttpHandler {

	public ActionMoveTaskGroupDownHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange)
			throws IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Failed to parse POST data.");
		}
		String id = CommonTask.getTaskGroupIdFromRequest(backEndHolder, params);
		if (id == null || id.isEmpty()) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Cannot find task group from request data.");
		}

		moveTaskGroupDown(id);
		return renderedTaskGroups(exchange);
	}
}
