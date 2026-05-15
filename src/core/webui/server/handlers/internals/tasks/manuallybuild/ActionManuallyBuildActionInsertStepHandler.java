package core.webui.server.handlers.internals.tasks.manuallybuild;

import argo.jdom.JsonNode;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedManuallyBuildSteps;
import core.webui.webcommon.HTTPLogger;
import core.webui.webcommon.HttpServerUtilities;




import java.util.HashMap;
import java.util.Map;

public final class ActionManuallyBuildActionInsertStepHandler extends AbstractUIHttpHandler {

    private final static HTTPLogger LOGGER = new HTTPLogger("Could not add action step");
    private final ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

    public ActionManuallyBuildActionInsertStepHandler(ObjectRenderer objectRenderer, ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
        this.manuallyBuildActionConstructorManager = manuallyBuildActionConstructorManager;
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        LOGGER.exec(() -> {
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

            if (!params.isNumberValue("index")) {
                HttpServerUtilities.prepareHttpResponse(exchange, 400, "No index provided."); 
return;
            }

            int index = Integer.parseInt(params.getNumberValue("index"));
            if (index < 0) {
                index = 0;
            }

            ManuallyBuildActionConstructor constructor = manuallyBuildActionConstructorManager.get(id);
            if (constructor == null) {
                HttpServerUtilities.prepareHttpResponse(exchange, 400, "No builder for ID " + id + "."); 
return;
            }
            if (index >= constructor.getSteps().size()) {
                index = Math.max(0, constructor.getSteps().size() - 1);
            }

            ManuallyBuildStep step;
            try {
                step = getStepFromRequest(params);
            } catch (InvalidManuallyBuildComponentException e) {
                HttpServerUtilities.prepareHttpResponse(exchange, 400, e.getMessage()); 
return;
            }
            if (step == null) {
                HttpServerUtilities.prepareHttpResponse(exchange, 500, "Cannot parse step."); 
return;
            }

            constructor.addStep(index, step);

            Map<String, Object> data = new HashMap<>();
            data.put("constructor", RenderedManuallyBuildSteps.fromManuallyBuildActionConstructor(constructor));
            String page = objectRenderer.render("fragments/task_builder_steps_table_rendered", data);
            if (page == null) {
                HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page."); 
return;
            }
            HttpServerUtilities.prepareHttpResponse(exchange, 200, page); 
return;

        }, exchange);
    }

    private ManuallyBuildStep getStepFromRequest(JsonNode params) throws InvalidManuallyBuildComponentException {
        if (!params.isStringValue("actor")) {
            throw new InvalidManuallyBuildComponentException("Parameter 'actor' must be provided.");
        }
        if (!params.isStringValue("action")) {
            throw new InvalidManuallyBuildComponentException("Parameter 'action' must be provided.");
        }
        if (!params.isStringValue("parameters")) {
            throw new InvalidManuallyBuildComponentException("Parameter 'parameters' must be provided.");
        }

        String actor = params.getStringValue("actor").toLowerCase();
        String action = params.getStringValue("action").toLowerCase();
        String parameters = params.getStringValue("parameters");
        return ManuallyBuildActionParametersParser.of().parse(actor, action, parameters);
    }
}
