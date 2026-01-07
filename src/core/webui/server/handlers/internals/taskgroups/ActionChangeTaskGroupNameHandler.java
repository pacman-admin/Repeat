package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.userDefinedTask.TaskGroup;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class ActionChangeTaskGroupNameHandler extends AbstractUIHttpHandler {

	public ActionChangeTaskGroupNameHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange)
			throws IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get parameters.");
		}
		TaskGroup group = CommonTask.getTaskGroupFromRequest(backEndHolder, params, false);
		if (group == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Unable to get task group.");
		}

		String name = params.get("name");
		if (name == null || name.isBlank()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Group name must be provided.");
		}

		group.setName(name);
		return renderedTaskGroups(exchange);
	}
}
