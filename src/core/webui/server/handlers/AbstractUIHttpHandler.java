package core.webui.server.handlers;

import core.ipc.IPCServiceManager;
import core.languageHandler.Language;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.TaskGroupManager;
import core.webui.server.handlers.renderedobjects.*;
import core.webui.webcommon.HTTPLogger;
import core.webui.webcommon.HttpServerUtilities;
import main.Backend;
import org.apache.http.HttpStatus;
import org.apache.http.nio.protocol.HttpAsyncExchange;

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

    protected final Void renderedIpcServices(HttpAsyncExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("ipcs", List.of(RenderedIPCService.fromIPCService(IPCServiceManager.getUIServer())));
        return renderedPage(exchange, "fragments/ipcs", data);
    }

    protected final Void renderedTaskForGroup(HttpAsyncExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        TaskGroup group = TaskGroupManager.getCurrentTaskGroup();
        List<RenderedUserDefinedAction> taskList = group.getTasks().stream().map(RenderedUserDefinedAction::fromUserDefinedAction).collect(Collectors.toList());
        data.put("tooltips", new TooltipsIndexPage());
        data.put("tasks", taskList);
        return renderedPage(exchange, "fragments/tasks", data);
    }

    protected final Void renderedTaskGroups(HttpAsyncExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("groups", TaskGroupManager.getTaskGroups().stream().map(g -> RenderedTaskGroup.fromTaskGroup(g, g == TaskGroupManager.getCurrentTaskGroup())).collect(Collectors.toList()));
        return renderedPage(exchange, "fragments/task_groups", data);
    }

    protected final Void renderedCompilingLanguages(HttpAsyncExchange exchange) throws IOException {
        Language selected = Backend.getSelectedLanguage();
        Map<String, Object> data = new HashMap<>();
        List<RenderedCompilingLanguage> languages = new ArrayList<>();
        for (Language language : Language.values()) {
            languages.add(RenderedCompilingLanguage.forLanguage(language, language == selected));
        }
        data.put("compilingLanguages", languages);
        return renderedPage(exchange, "fragments/compiling_languages", data);
    }

    protected final Void renderedPage(HttpAsyncExchange exchange, String template, Map<String, Object> data) throws IOException {
        return LOGGER.exec(() -> {
            String page = objectRenderer.render(template, data);
            if (page == null) {
                return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
            }
            return HttpServerUtilities.prepareHttpResponse(exchange, HttpStatus.SC_OK, page);
        }, exchange);
    }
}