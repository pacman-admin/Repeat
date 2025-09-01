package core.webui.server.handlers.internals.globalconfigs;

import java.io.IOException;
import java.util.List;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.renderedobjects.ObjectRenderer;

public class SetToolsConfigClientsHandler extends AbstractSetRemoteRepeatsClientsHandler {

	public SetToolsConfigClientsHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer);
	}

	@Override
	public void setConfig(List<String> clientIds) {
		backEndHolder.setToolsClients(clientIds);
	}

	@Override
	public Void renderResponse(HttpAsyncExchange exchange) throws IOException {
		return renderedToolsClientsConfig(exchange);
	}
}
