package core.webui.server.handlers.internals.tasks;

import core.userDefinedTask.UserDefinedAction;
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

public final class ToggleTaskEnabledHandler extends AbstractUIHttpHandler {

    public ToggleTaskEnabledHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange)
            throws IOException {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
        if (params == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get parameters."); 
return;
        }

        UserDefinedAction task = CommonTask.getTaskFromRequest(params);
        if (task == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get task."); 
return;
        }

        Backend.switchEnableTask(task);
        renderedTaskForGroup(exchange);
    }
}
