package core.webui.server.handlers.internals.ipcs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedIPCService;
import core.webui.server.handlers.renderedobjects.TooltipsIPCPage;

public class IPCPageHandler extends AbstractUIHttpHandler {

	public IPCPageHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("ipcs", List.of(RenderedIPCService.fromIPCService(IPCServiceManager.getUIServer())));
		data.put("tooltips", new TooltipsIPCPage());
		return renderedPage(exchange, "ipcs", data);
	}
}