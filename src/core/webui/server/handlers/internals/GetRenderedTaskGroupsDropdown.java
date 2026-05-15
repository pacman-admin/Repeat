package core.webui.server.handlers.internals;

import java.util.HashMap;
import java.util.Map;

import core.userDefinedTask.TaskGroupManager;




import core.userDefinedTask.TaskGroup;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedTaskGroupButton;
import core.webui.webcommon.HttpServerUtilities;

public final class GetRenderedTaskGroupsDropdown extends AbstractUIHttpHandler {

	public GetRenderedTaskGroupsDropdown(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange) {
		Map<String, Object> data = new HashMap<>();
		TaskGroup group = TaskGroupManager.getCurrentTaskGroup();
		data.put("taskGroup", RenderedTaskGroupButton.fromTaskGroups(group, TaskGroupManager.getTaskGroups()));

		String page = objectRenderer.render("fragments/task_groups_dropdown", data);
		if (page == null) {
			HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page."); 
return;
		}

		HttpServerUtilities.prepareHttpResponse(exchange, 200, page);
    }
}
