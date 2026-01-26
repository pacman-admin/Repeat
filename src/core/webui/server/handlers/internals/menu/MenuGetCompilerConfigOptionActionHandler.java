package core.webui.server.handlers.internals.menu;

import core.languageHandler.Language;
import core.languageHandler.compiler.JavaNativeCompiler;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import utilities.StringUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class MenuGetCompilerConfigOptionActionHandler extends AbstractUIHttpHandler {

    public MenuGetCompilerConfigOptionActionHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) throws IOException {
        Language language = Backend.getSelectedLanguage();
        if (language != Language.JAVA) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Current language " + language.name() + " does not support changing configuration.");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("classPaths", "");
        return renderedPage(exchange, "fragments/java_compiler_configuration", data);
    }
}
