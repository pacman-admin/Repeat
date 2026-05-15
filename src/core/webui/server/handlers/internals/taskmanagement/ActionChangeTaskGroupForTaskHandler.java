package core.webui.server.handlers.internals.taskmanagement;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;



import java.io.IOException;
import java.util.Map;

public final class ActionChangeTaskGroupForTaskHandler extends AbstractUIHttpHandler {

    public ActionChangeTaskGroupForTaskHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) throws IOException {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
        if (params == null) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Failed to parse POST data."); 
return;
        }
        String groupId = CommonTask.getTaskGroupIdFromRequest( params);
        if (groupId == null || groupId.isBlank()) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to get group ID."); 
return;
        }
        String taskId = CommonTask.getTaskIdFromRequest(params);
        if (taskId.isBlank()) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to get task ID."); 
return;
        }

        Backend.changeTaskGroup(taskId, groupId);
        renderedTaskForGroup(exchange);
    }
}
