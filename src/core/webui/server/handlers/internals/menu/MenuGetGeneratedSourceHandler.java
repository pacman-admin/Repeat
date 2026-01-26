package core.webui.server.handlers.internals.menu;

import argo.jdom.JsonNode;
import core.languageHandler.Language;
import core.languageHandler.compiler.CompilationOutcome;
import core.languageHandler.compiler.CompilationResult;
import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractTaskSourceCodeHandler;
import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler;
import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler.RenderException;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public final class MenuGetGeneratedSourceHandler extends AbstractTaskSourceCodeHandler {

    public MenuGetGeneratedSourceHandler(TaskSourceCodeFragmentHandler taskSourceCodeFragmentHandler) {
        super(taskSourceCodeFragmentHandler, AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
        String source = Backend.generateSource();
        if (source == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to generate source code.");
        }

        Language language = Backend.getSelectedLanguage();
        UserDefinedAction action = null;
        if (language == Language.MANUAL_BUILD) {
            CompilationResult compilationResult = Backend.getCompiler().compile(source);
            if (compilationResult.outcome() != CompilationOutcome.COMPILATION_SUCCESS) {
                return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to compile generated source code.");
            }

            action = compilationResult.action();
        }
        try {
            JsonNode data = taskSourceCodeFragmentHandler.render(language, source, action);
            return HttpServerUtilities.prepareJsonResponse(exchange, 200, data);
        } catch (RenderException e) {
            return HttpServerUtilities.prepareTextResponse(exchange, 500, "Failed to render page: " + e.getMessage());
        }
    }
}
