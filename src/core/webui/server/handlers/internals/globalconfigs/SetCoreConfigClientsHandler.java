package core.webui.server.handlers.internals.globalconfigs;

import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;
import java.util.List;

public class SetCoreConfigClientsHandler extends AbstractSetRemoteRepeatsClientsHandler {

    public SetCoreConfigClientsHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer);
    }

    @Override
    public void setConfig(List<String> clientIds) {
        backEndHolder.setCoreClients(clientIds);
    }

    @Override
    public Void renderResponse(HttpAsyncExchange exchange) throws IOException {
        return renderedCoreClientsConfig(exchange);
    }
}
