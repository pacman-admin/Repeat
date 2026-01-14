package core.webui.server.handlers.internals.taskgroups;

import core.userDefinedTask.TaskGroup;
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

public final class ToggleTaskGroupEnabledHandler extends AbstractUIHttpHandler {

    public ToggleTaskGroupEnabledHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) throws IOException {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
        if (params == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get parameters.");
        }

        TaskGroup group = CommonTask.getTaskGroupFromRequest(params, false);
        if (group == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Unable to get task group from request parameters.");
        }

        group.setEnabled(!group.isEnabled(), Backend.INPUT_EVENT_MANAGER);
        return renderedTaskGroups(exchange);
    }
}
