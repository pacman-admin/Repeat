package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedTaskGroup;
import core.webui.server.handlers.renderedobjects.TooltipsTaskGroupsPage;

public class TaskGroupsPageHandler extends AbstractUIHttpHandler {

	public TaskGroupsPageHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("groups", backEndHolder.getTaskGroups()
				.stream().map(g -> RenderedTaskGroup.fromTaskGroup(g, g == backEndHolder.getCurrentTaskGroup()))
				.collect(Collectors.toList()));
		data.put("tooltips", new TooltipsTaskGroupsPage());

		return renderedPage(exchange, "task_groups", data);
	}
}
