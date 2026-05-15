package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import core.userDefinedTask.TaskGroupManager;



import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractUIHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedTaskGroup;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.TooltipsTaskGroupsPage;

public final class TaskGroupsPageHandler extends AbstractUIHttpHandler {

	public TaskGroupsPageHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange)
			throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("groups", TaskGroupManager.getTaskGroups()
				.stream().map(g -> RenderedTaskGroup.fromTaskGroup(g, g == TaskGroupManager.getCurrentTaskGroup()))
				.collect(Collectors.toList()));
		data.put("tooltips", new TooltipsTaskGroupsPage());

		renderedPage(exchange, "task_groups", data);
	}
}
