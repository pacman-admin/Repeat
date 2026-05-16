package core.webui.server.handlers.internals.taskactivation;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedDetailedUserDefinedAction;
import core.webui.webcommon.HTTPLogger;
import core.webui.webcommon.HttpServerUtilities;


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
    public final void handleAllowedRequestWithBackend(HttpExchange exchange) {
        LOGGER.exec(() -> {
            Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
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
            handleRequestWithBackendAndConstructor(exchange, constructor, params);
        }, exchange);
    }

    final void renderedTaskActivationPage(HttpExchange exchange, String template, TaskActivationConstructor constructor) {
        LOGGER.exec(() -> {
            Map<String, Object> data = new HashMap<>();
            data.put("task", RenderedDetailedUserDefinedAction.withEmptyTaskInfo(constructor));
            renderedPage(exchange, template, data);
        }, exchange);
    }

    abstract void handleRequestWithBackendAndConstructor(HttpExchange exchange, TaskActivationConstructor constructor, Map<String, String> params);
}