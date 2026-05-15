package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Map;




import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import utilities.StringUtil;

public final class ModifyTaskNameHandler extends AbstractUIHttpHandler {

	public ModifyTaskNameHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange)
			throws IOException {
		Map<String, String>  params = HttpServerUtilities.parseSimplePostParameters(exchange);
		if (params == null) {
			HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to parse POST parameters."); 
return;
		}
		UserDefinedAction task = CommonTask.getTaskFromRequest( params);
		if (task == null) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get task."); 
return;
		}

		String name = params.get("name");
		if (StringUtil.isNullOrEmpty(name)) {
			HttpServerUtilities.prepareHttpResponse(exchange, 500, "Name must be provided and not empty."); 
return;
		}

		task.setName(name);
		renderedTaskForGroup(exchange);
	}
}
