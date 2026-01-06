package core.webui.server.handlers.internals.logs;

import core.webui.server.handlers.AbstractBooleanGETHandler;

public class GetIsMousePositionLoggingEnabledHandler extends AbstractBooleanGETHandler {
    @Override
    protected boolean handle() {
        return backEndHolder.isMousePositionLoggingEnabled();
    }
}