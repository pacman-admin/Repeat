package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.util.logging.Logger;

public class MenuGetCompilerPathActionHandler extends AbstractSingleMethodHttpHandler {

    private static final Logger LOGGER = Logger.getLogger(MenuGetCompilerPathActionHandler.class.getName());

    public MenuGetCompilerPathActionHandler() {
        super(AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
        if (!backEndHolder.getCompiler().canSetPath()) {
            LOGGER.info("Current compiler does not support getting/setting path.");
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Current compiler does not support getting/setting path.");
        }
        String result = backEndHolder.getCompiler().getPath().getAbsolutePath();
        return HttpServerUtilities.prepareTextResponse(exchange, 200, result);
    }
}
