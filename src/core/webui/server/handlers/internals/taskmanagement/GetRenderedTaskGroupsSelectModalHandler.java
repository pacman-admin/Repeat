package core.webui.server.handlers.internals.taskmanagement;

import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.TaskGroupManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedTaskGroupForSelectModal;
import core.webui.webcommon.HttpServerUtilities;




import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GetRenderedTaskGroupsSelectModalHandler extends AbstractUIHttpHandler {

    public GetRenderedTaskGroupsSelectModalHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        Map<String, Object> data = new HashMap<>();
        TaskGroup group = TaskGroupManager.getCurrentTaskGroup();
        List<TaskGroup> groups = TaskGroupManager.getTaskGroups();
        data.put("groups", groups.stream().map(g -> RenderedTaskGroupForSelectModal.fromTaskGroups(g, group)).collect(Collectors.toList()));

        String page = objectRenderer.render("fragments/task_groups_select", data);
        if (page == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page."); 
return;
        }

        HttpServerUtilities.prepareHttpResponse(exchange, 200, page);
    }
}
