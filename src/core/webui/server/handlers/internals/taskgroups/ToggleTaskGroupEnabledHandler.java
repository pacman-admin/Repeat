package core.webui.server.handlers.internals.taskgroups;

import core.userDefinedTask.TaskGroup;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractUIHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.CommonTask;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;



import java.io.IOException;
import java.util.Map;

public final class ToggleTaskGroupEnabledHandler extends AbstractUIHttpHandler {

    public ToggleTaskGroupEnabledHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) throws IOException {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
        if (params == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get parameters."); 
return;
        }

        TaskGroup group = CommonTask.getTaskGroupFromRequest(params, false);
        if (group == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Unable to get task group from request parameters."); 
return;
        }

        group.setEnabled(!group.isEnabled(), Backend.keysManager);
        renderedTaskGroups(exchange);
    }
}
