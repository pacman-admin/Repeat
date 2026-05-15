package core.webui.server.handlers.internals.taskcreation;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractBooleanGETHandler;
import frontEnd.Backend;

public final class GetIsRunningCompiledTaskHandler extends AbstractBooleanGETHandler {
    @Override
    protected boolean handle() {
        return Backend.isRunningCompiledAction();
    }
}