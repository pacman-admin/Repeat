package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.Map;




import core.userDefinedTask.TaskGroup;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public final class ActionChangeTaskGroupNameHandler extends AbstractUIHttpHandler {

	public ActionChangeTaskGroupNameHandler(ObjectRenderer objectRenderer) {
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
		TaskGroup group = CommonTask.getTaskGroupFromRequest( params, false);
		if (group == null) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "Unable to get task group."); 
return;
		}

		String name = params.get("name");
		if (name == null || name.isBlank()) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "Group name must be provided."); 
return;
		}

		group.setName(name);
		renderedTaskGroups(exchange);
	}
}
