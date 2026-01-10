package core.webui.server.handlers.internals.tasks;

import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;
import java.util.Map;

public class ToggleTaskEnabledHandler extends AbstractUIHttpHandler {

    public ToggleTaskEnabledHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange)
            throws IOException {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
        if (params == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get parameters.");
        }

        UserDefinedAction task = CommonTask.getTaskFromRequest(params);
        if (task == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get task.");
        }

        Backend.switchEnableTask(task);
        return renderedTaskForGroup(exchange);
    }
}
