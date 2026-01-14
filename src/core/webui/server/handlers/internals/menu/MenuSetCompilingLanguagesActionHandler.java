package core.webui.server.handlers.internals.menu;

import java.util.Map;

import main.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import argo.jdom.JsonNode;
import core.languageHandler.Language;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractTaskSourceCodeHandler;
import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler;
import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler.RenderException;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

public final class MenuSetCompilingLanguagesActionHandler extends AbstractTaskSourceCodeHandler {

	public MenuSetCompilingLanguagesActionHandler(TaskSourceCodeFragmentHandler taskSourceCodeFragmentHandler) {
		super(taskSourceCodeFragmentHandler, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse POST parameters.");
		}
		String indexString = params.get("index");
		if (indexString == null || !NumberUtility.isNonNegativeInteger(indexString)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Index must be provided as non-negative integer.");
		}
		Language language = Language.identify(Integer.parseInt(indexString));
		if (language == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Language index " + indexString + " unknown.");
		}

		try {
			JsonNode data = taskSourceCodeFragmentHandler.render(language);
			Backend.setCompilingLanguage(language);
			return HttpServerUtilities.prepareJsonResponse(exchange, 200, data);
		} catch (RenderException e) {
			return HttpServerUtilities.prepareTextResponse(exchange, 500, "Failed to render page: " + e.getMessage());
		}
	}
}
