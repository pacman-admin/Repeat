package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedConfig;
import frontEnd.Backend;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class MenuGetDebugLevelOptionsActionHandler extends AbstractUIHttpHandler {

    public MenuGetDebugLevelOptionsActionHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("config", new RenderedConfig(Backend.config, Backend.recorder));
        renderedPage(exchange, "fragments/debug_levels", data);
    }
}
