package core.webui.server.handlers.internals.tasks.manuallybuild;

import argo.jdom.JsonNode;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedManuallyBuildSteps;
import core.webui.webcommon.HttpServerUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ActionManuallyBuildActionMoveDownHandler extends AbstractUIHttpHandler {

    private final ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

    public ActionManuallyBuildActionMoveDownHandler(ObjectRenderer objectRenderer, ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);

        this.manuallyBuildActionConstructorManager = manuallyBuildActionConstructorManager;
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        JsonNode params = HttpServerUtilities.parsePostParameters(exchange);
        if (params == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
            return;
        }

        if (!params.isStringValue("id")) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "No builder ID provided.");
            return;
        }

        String id = params.getStringValue("id");
        if (id == null || id.isBlank()) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "No builder ID provided.");
            return;
        }

        if (!params.isArrayNode("indices")) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Indices is not an array node.");
            return;
        }

        List<JsonNode> indicesNodes = params.getArrayNode("indices");
        if (indicesNodes.stream().anyMatch(n -> !n.isNumberValue())) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Indices must all be integers.");
            return;
        }
        // Get indices, smallest one first.
        List<Integer> indices = indicesNodes.stream().map(n -> Integer.parseInt(n.getNumberValue())).sorted((i1, i2) -> Integer.compare(i2, i1)).toList();

        ManuallyBuildActionConstructor constructor = manuallyBuildActionConstructorManager.get(id);
        // Since the list of indices is sorted with the largest one first, it's safe to move them sequentially.
        indices.stream().forEach(constructor::moveStepDown);

        Map<String, Object> data = new HashMap<>();
        data.put("constructor", RenderedManuallyBuildSteps.fromManuallyBuildActionConstructor(constructor));
        String page = objectRenderer.render("fragments/task_builder_steps_table_rendered", data);
        if (page == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
            return;
        }
        HttpServerUtilities.prepareHttpResponse(exchange, 200, page);
        return;
    }
}
