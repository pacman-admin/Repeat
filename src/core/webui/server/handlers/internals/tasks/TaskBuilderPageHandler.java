package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Map;


import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionBuilderBody;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;

public final class TaskBuilderPageHandler extends AbstractUIHttpHandler {

	private final ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

	public TaskBuilderPageHandler(ObjectRenderer objectRenderer, ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
		this.manuallyBuildActionConstructorManager = manuallyBuildActionConstructorManager;
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange)
			throws IOException {
		String id = manuallyBuildActionConstructorManager.addNew();
		Map<String, Object> data = ManuallyBuildActionBuilderBody.bodyData(manuallyBuildActionConstructorManager, id);
		renderedPage(exchange, "task_builder", data);
	}
}
