package core.webui.server.handlers.internals.taskactivation;

import java.io.IOException;
import java.util.Map;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

public class ActionTaskActivationRemovePhraseHandler extends AbstractTaskActivationConstructorActionHandler {

	public ActionTaskActivationRemovePhraseHandler(ObjectRenderer objectRenderer,
			TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, taskActivationConstructorManager);
	}

	@Override
	protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange,
			TaskActivationConstructor constructor, Map<String, String> params) throws IOException {
		String index = params.get("index");
		if (!NumberUtility.isNonNegativeInteger(index)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Index must be non-negative integer.");
		}

		constructor.removePhrase(Integer.parseInt(index));
		return renderedTaskActivationPage(exchange, "fragments/phrases", constructor);
	}
}
