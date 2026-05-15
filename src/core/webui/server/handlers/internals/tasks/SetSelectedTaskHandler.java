package core.webui.server.handlers.internals.tasks;

import java.util.Map;

import frontEnd.Backend;



import argo.jdom.JsonNode;
import core.languageHandler.Language;
import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractTaskSourceCodeHandler;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler.RenderException;
import core.webui.webcommon.HttpServerUtilities;

public final class SetSelectedTaskHandler extends AbstractTaskSourceCodeHandler {

	public SetSelectedTaskHandler(TaskSourceCodeFragmentHandler taskSourceCodeFragmentHandler) {
		super(taskSourceCodeFragmentHandler, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange) {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
		if (params == null) {
			HttpServerUtilities.prepareTextResponse(exchange, 500, "Unable to parse GET request parameters."); 
return;
		}
		String taskId = params.get("task");
		if (taskId.isBlank()) {
			HttpServerUtilities.prepareTextResponse(exchange, 400, "Task ID must be provided."); 
return;
		}

		UserDefinedAction action = Backend.getTask(taskId);
		Language language = action.getCompiler();


		try {
			JsonNode data = taskSourceCodeFragmentHandler.render(language, action.getSource(), action);
			Backend.setCompilingLanguage(language);
			HttpServerUtilities.prepareJsonResponse(exchange, 200, data); 
return;
		} catch (RenderException e) {
			HttpServerUtilities.prepareTextResponse(exchange, 500, "Failed to render page: " + e.getMessage()); 
return;
		}
	}
}