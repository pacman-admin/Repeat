package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedConfig;
import frontEnd.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MenuGetDebugLevelOptionsActionHandler extends AbstractUIHttpHandler {

    public MenuGetDebugLevelOptionsActionHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("config", new RenderedConfig(Backend.config, Backend.recorder));
        return renderedPage(exchange, "fragments/debug_levels", data);
    }
}
