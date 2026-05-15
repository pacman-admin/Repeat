package core.webui.server.handlers.internals.menu;

import java.util.Map;

import frontEnd.Backend;



import argo.jdom.JsonNode;
import core.languageHandler.Language;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractTaskSourceCodeHandler;
import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler.RenderException;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

public final class MenuSetCompilingLanguagesActionHandler extends AbstractTaskSourceCodeHandler {

	public MenuSetCompilingLanguagesActionHandler(TaskSourceCodeFragmentHandler taskSourceCodeFragmentHandler) {
		super(taskSourceCodeFragmentHandler, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange) {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
		if (params == null) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse POST parameters."); 
return;
		}
		String indexString = params.get("index");
		if (indexString == null || !NumberUtility.isNonNegativeInteger(indexString)) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "Index must be provided as non-negative integer."); 
return;
		}
		Language language = Language.identify(Integer.parseInt(indexString));
		if (language == null) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "Language index " + indexString + " unknown."); 
return;
		}

		try {
			JsonNode data = taskSourceCodeFragmentHandler.render(language);
			Backend.setCompilingLanguage(language);
			HttpServerUtilities.prepareJsonResponse(exchange, 200, data);
        } catch (RenderException e) {
			HttpServerUtilities.prepareTextResponse(exchange, 500, "Failed to render page: " + e.getMessage());
        }
	}
}
