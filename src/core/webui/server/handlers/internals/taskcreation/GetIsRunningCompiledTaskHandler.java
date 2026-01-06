package core.webui.server.handlers.internals.taskcreation;

import core.webui.server.handlers.AbstractBooleanGETHandler;

public class GetIsRunningCompiledTaskHandler extends AbstractBooleanGETHandler {
    @Override
    protected boolean handle() {
        return backEndHolder.isRunningCompiledAction();

    }
}