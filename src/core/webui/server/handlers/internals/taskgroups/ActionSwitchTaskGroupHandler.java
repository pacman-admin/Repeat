package core.webui.server.handlers.internals.taskgroups;

import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.TaskGroupManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;


import java.io.IOException;
import java.util.Map;

public final class ActionSwitchTaskGroupHandler extends AbstractUIHttpHandler {

    public ActionSwitchTaskGroupHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) throws IOException {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
        if (params == null) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to get POST parameters.");
            return;
        }

        String rendering = params.get("render");
        if (rendering == null) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Missing `render` parameter.");
            return;
        }
        if (!rendering.equals("tasks") && !rendering.equals("groups")) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Render parameter only takes `tasks` or `groups`.");
            return;
        }

        TaskGroup newCurrent = CommonTask.getTaskGroupFromRequest(params, false);
        if (newCurrent == null) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Cannot get task group from exchange.");
            return;
        }

        TaskGroupManager.setCurrentTaskGroup(newCurrent);

        if (rendering.equals("tasks")) {
            renderedTaskForGroup(exchange);
        }
        renderedTaskGroups(exchange);
    }
}