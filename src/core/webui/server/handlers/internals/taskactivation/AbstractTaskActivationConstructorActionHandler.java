package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedDetailedUserDefinedAction;
import core.webui.webcommon.HTTPLogger;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractTaskActivationConstructorActionHandler extends AbstractUIHttpHandler {

    private static final HTTPLogger LOGGER = new HTTPLogger("Could not handle task activation modification action.");
    private final TaskActivationConstructorManager taskActivationConstructorManager;

    AbstractTaskActivationConstructorActionHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
        this.taskActivationConstructorManager = taskActivationConstructorManager;
    }

    @Override
    protected final Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
        return LOGGER.exec(() -> {
            Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
            if (params == null) {
                throw new IllegalArgumentException("Failed to get POST parameters.");
            }
            String id = params.get("id");
            if (id == null) {
                throw new IllegalArgumentException("Failed to get task activation constructor ID.");
            }
            TaskActivationConstructor constructor = taskActivationConstructorManager.get(id);
            if (constructor == null) {
                throw new NullPointerException("No constructor found for ID '" + id + "'.");
            }
            return handleRequestWithBackendAndConstructor(exchange, constructor, params);
        }, exchange);
    }

    final Void renderedTaskActivationPage(HttpAsyncExchange exchange, String template, TaskActivationConstructor constructor) throws IOException {
        return LOGGER.exec(() -> {
            Map<String, Object> data = new HashMap<>();
            data.put("task", RenderedDetailedUserDefinedAction.withEmptyTaskInfo(constructor));
            return renderedPage(exchange, template, data);
        }, exchange);
    }

    abstract Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) throws IOException;
}