package core.webui.server.handlers.internals.ipcs;

import java.io.IOException;
import java.util.Map;


import core.ipc.IIPCService;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

public final class ModifyIPCServicePortHandler extends AbstractUIHttpHandler {

	public ModifyIPCServicePortHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange)
			throws IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
		if (params == null) {
			HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get POST parameters."); 
return;
		}

		IIPCService service = CommonTask.getIPCService(params);
		if (service == null) {
			HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get IPC service."); 
return;
		}

		String portString = params.get("port");
		if (portString == null || !NumberUtility.isNonNegativeInteger(portString)) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "Port must be non-negative integer."); 
return;
		}

		int port = Integer.parseInt(portString);
		if (port < 1024 || port > 65535) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "Port must be integer between 1024 and 65535"); 
return;
		}

		service.setPort(port);
		renderedIpcServices(exchange);
	}
}
