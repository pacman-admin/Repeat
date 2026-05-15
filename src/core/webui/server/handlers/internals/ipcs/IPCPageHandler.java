package core.webui.server.handlers.internals.ipcs;

import com.sun.net.httpserver.HttpExchange;
import core.ipc.IPCServiceManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractUIHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedIPCService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.TooltipsIPCPage;



import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IPCPageHandler extends AbstractUIHttpHandler {

    public IPCPageHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange)
            throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("ipcs", List.of(RenderedIPCService.of(IPCServiceManager.getUIServer())));
        data.put("tooltips", new TooltipsIPCPage());
        renderedPage(exchange, "ipcs", data);
    }
}