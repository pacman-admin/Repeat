package core.webui.server.handlers.internals.taskmanagement;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractUIHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import frontEnd.Backend;



import java.io.IOException;

public final class ActionAddTaskHandler extends AbstractUIHttpHandler {

    public ActionAddTaskHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) throws IOException {
        Backend.addCurrentTask();
        renderedTaskForGroup(exchange);
    }
}
