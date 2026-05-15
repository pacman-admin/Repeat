package core.webui.server.handlers;

import core.ipc.IPCServiceManager;
import core.languageHandler.Language;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.TaskGroupManager;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.*;
import core.webui.webcommon.HTTPLogger;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractUIHttpHandler extends AbstractSingleMethodHttpHandler {
    private static final HTTPLogger LOGGER = new HTTPLogger("UI error.");
    protected final ObjectRenderer objectRenderer;

    public AbstractUIHttpHandler(ObjectRenderer objectRenderer, String allowedMethod) {
        super(allowedMethod);
        this.objectRenderer = objectRenderer;
    }

    protected final void renderedIpcServices(HttpExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("ipcs", List.of(RenderedIPCService.of(IPCServiceManager.getUIServer())));
        renderedPage(exchange, "fragments/ipcs", data);
    }

    protected final void renderedTaskForGroup(HttpExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        TaskGroup group = TaskGroupManager.getCurrentTaskGroup();
        List<RenderedUserDefinedAction> taskList = group.getTasks().stream().map(RenderedUserDefinedAction::fromUserDefinedAction).collect(Collectors.toList());
        data.put("tooltips", new TooltipsIndexPage());
        data.put("tasks", taskList);
        renderedPage(exchange, "fragments/tasks", data);
    }

    protected final void renderedTaskGroups(HttpExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("groups", TaskGroupManager.getTaskGroups().stream().map(g -> RenderedTaskGroup.fromTaskGroup(g, g == TaskGroupManager.getCurrentTaskGroup())).collect(Collectors.toList()));
        renderedPage(exchange, "fragments/task_groups", data);
    }

    protected final void renderedCompilingLanguages(HttpExchange exchange) throws IOException {
        Language selected = Backend.getSelectedLanguage();
        Map<String, Object> data = new HashMap<>();
        List<RenderedCompilingLanguage> languages = new ArrayList<>();
        for (Language language : Language.values()) {
            languages.add(RenderedCompilingLanguage.forLanguage(language, language == selected));
        }
        data.put("compilingLanguages", languages);
        renderedPage(exchange, "fragments/compiling_languages", data);
    }

    protected final void renderedPage(HttpExchange exchange, String template, Map<String, Object> data) throws IOException {
        LOGGER.exec(() -> {
            String page = objectRenderer.render(template, data);
            if (page == null) {
                HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
                return;
            }
            HttpServerUtilities.prepareHttpResponse(exchange, 200, page);
        }, exchange);
    }
}