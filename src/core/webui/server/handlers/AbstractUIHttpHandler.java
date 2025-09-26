package core.webui.server.handlers;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;
import core.languageHandler.Language;
import core.userDefinedTask.TaskGroup;
import core.webui.server.handlers.renderedobjects.*;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpStatus;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractUIHttpHandler extends AbstractSingleMethodHttpHandler {

    protected ObjectRenderer objectRenderer;

    public AbstractUIHttpHandler(ObjectRenderer objectRenderer, String allowedMethod) {
        super(allowedMethod);
        this.objectRenderer = objectRenderer;
    }

    protected final Void renderedIpcServices(HttpAsyncExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        List<RenderedIPCService> services = new ArrayList<>(IPCServiceManager.IPC_SERVICE_COUNT);
        for (int i = 0; i < IPCServiceManager.IPC_SERVICE_COUNT; i++) {
            IIPCService service = IPCServiceManager.getIPCService(i);
            assert service != null;
            services.add(RenderedIPCService.fromIPCService(service));
        }
        data.put("ipcs", services);

        return renderedPage(exchange, "fragments/ipcs", data);
    }

    protected final Map<String, Object> getGlobalConfigRenderingData() {
        Map<String, Object> data = new HashMap<>();
        RenderedRemoteRepeatsClientsConfig toolsConfig = RenderedRemoteRepeatsClientsConfig.of(backEndHolder.getPeerServiceClientManager(), backEndHolder.getConfig().getToolsConfig());
        RenderedRemoteRepeatsClientsConfig coreConfig = RenderedRemoteRepeatsClientsConfig.of(backEndHolder.getPeerServiceClientManager(), backEndHolder.getConfig().getCoreConfig());
        RenderedRemoteRepeatsClientsConfig remoteRepeatsCompilerConfig = RenderedRemoteRepeatsClientsConfig.of(backEndHolder.getPeerServiceClientManager(), backEndHolder.getConfig().getCompilerFactory().getRemoteRepeatsCompilerConfig());
        data.put("globalConfigs", RenderedGlobalConfigs.of(toolsConfig, coreConfig, remoteRepeatsCompilerConfig));

        return data;
    }

    protected final Void renderedToolsClientsConfig(HttpAsyncExchange exchange) throws IOException {
        return renderedPage(exchange, "fragments/tools_clients", getGlobalConfigRenderingData());
    }

    protected final Void renderedCoreClientsConfig(HttpAsyncExchange exchange) throws IOException {
        return renderedPage(exchange, "fragments/core_clients", getGlobalConfigRenderingData());
    }

    protected final Void renderedTaskForGroup(HttpAsyncExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        TaskGroup group = backEndHolder.getCurrentTaskGroup();
        List<RenderedUserDefinedAction> taskList = group.getTasks().stream().map(RenderedUserDefinedAction::fromUserDefinedAction).collect(Collectors.toList());
        data.put("tooltips", new TooltipsIndexPage());
        data.put("tasks", taskList);
        return renderedPage(exchange, "fragments/tasks", data);
    }

    protected final Void renderedTaskGroups(HttpAsyncExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("groups", backEndHolder.getTaskGroups().stream().map(g -> RenderedTaskGroup.fromTaskGroup(g, g == backEndHolder.getCurrentTaskGroup())).collect(Collectors.toList()));
        return renderedPage(exchange, "fragments/task_groups", data);
    }

    protected final Void renderedCompilingLanguages(HttpAsyncExchange exchange) throws IOException {
        Language selected = backEndHolder.getSelectedLanguage();
        Map<String, Object> data = new HashMap<>();
        List<RenderedCompilingLanguage> languages = new ArrayList<>();
        for (Language language : Language.values()) {
            languages.add(RenderedCompilingLanguage.forLanguage(language, language == selected));
        }
        data.put("compilingLanguages", languages);
        return renderedPage(exchange, "fragments/compiling_languages", data);
    }

    protected final Void renderedPage(HttpAsyncExchange exchange, String template, Map<String, Object> data) throws IOException {
        String page = objectRenderer.render(template, data);
        if (page == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
        }
        return HttpServerUtilities.prepareHttpResponse(exchange, HttpStatus.SC_OK, page);
    }
}