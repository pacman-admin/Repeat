package core.webui.server.handlers.internals.tasks.manuallybuild;

import core.languageHandler.Language;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;




import java.util.Map;

public final class ActionManuallyBuildActionBuildAction extends AbstractSingleMethodHttpHandler {

    private final ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

    public ActionManuallyBuildActionBuildAction(ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
        this.manuallyBuildActionConstructorManager = manuallyBuildActionConstructorManager;
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
        if (params == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters."); 
return;
        }

        String id = params.get("id");
        if (id == null || id.isBlank()) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "No builder ID provided."); 
return;
        }

        ManuallyBuildActionConstructor constructor = manuallyBuildActionConstructorManager.get(id);
        String source = constructor.generateSource();
        Backend.setCompilingLanguage(Language.MANUAL_BUILD);
        if (!Backend.compileSourceAndSetCurrent(source, null)) {
            HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to compile generated source code."); 
return;
        }
        HttpServerUtilities.prepareHttpResponse(exchange, 200, ""); 
return;
    }
}
