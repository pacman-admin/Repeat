package core.webui.server.handlers.internals.taskcreation;

import core.webui.server.handlers.AbstractBooleanGETHandler;
import main.Backend;

public final class GetIsRunningCompiledTaskHandler extends AbstractBooleanGETHandler {
    @Override
    protected boolean handle() {
        return Backend.isRunningCompiledAction();
    }
}