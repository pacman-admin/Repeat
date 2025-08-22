package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import utilities.StringUtilities;

public class ModifyTaskNameHandler extends AbstractUIHttpHandler {

	public ModifyTaskNameHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws IOException {
		Map<String, String>  params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to parse POST parameters.");
		}
		UserDefinedAction task = CommonTask.getTaskFromRequest(backEndHolder, params);
		if (task == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get task.");
		}

		String name = params.get("name");
		if (StringUtilities.isNullOrEmpty(name)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Name must be provided and not empty.");
		}

		task.setName(name);
		return renderedTaskForGroup(exchange);
	}
}
