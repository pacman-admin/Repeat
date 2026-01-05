package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MenuSetCompilerPathActionHandler extends AbstractSingleMethodHttpHandler {

    public MenuSetCompilerPathActionHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
        if (params == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse POST parameters.");
        }

        String path = params.get("path");
        if (path == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Path must be provided.");
        }

        if (!backEndHolder.getCompiler().canSetPath()) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Path cannot be set for current compiler.");
        }

        if (!backEndHolder.getCompiler().setPath(new File(path))) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Cannot set path '" + path + "' for current compiler.");
        }

        /*

        Language language = backEndHolder.getSelectedLanguage();
        if (language == Language.PYTHON) {
            File pythonExecutable = ((PythonRemoteCompiler) (backEndHolder.getConfig().getCompilerFactory()).getNativeCompiler(Language.PYTHON)).getPath();
            ((PythonIPCClientService) IPCServiceManager.getIPCService(IPCServiceName.PYTHON)).setExecutingProgram(pythonExecutable);
        }*/

        return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
    }
}
