package core.webui.server.handlers.internals.taskmanagement;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Map;

public class ActionOverwriteTaskHandler extends AbstractUIHttpHandler {

    public ActionOverwriteTaskHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws IOException {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
        if (params == null) {
            return HttpServerUtilities.prepareTextResponse(exchange, 400, "Failed to parse POST data.");
        }

        String taskId = CommonTask.getTaskIdFromRequest(backEndHolder, params);
        if (taskId == null || taskId.isEmpty()) {
            return HttpServerUtilities.prepareTextResponse(exchange, 400, "Cannot find task from request data.");
        }

        backEndHolder.overwriteTask(taskId);
        return renderedTaskForGroup(exchange);
    }
}
