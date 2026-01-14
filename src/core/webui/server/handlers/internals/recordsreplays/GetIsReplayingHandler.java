package core.webui.server.handlers.internals.recordsreplays;

import core.webui.server.handlers.AbstractBooleanGETHandler;
import main.Backend;

public final class GetIsReplayingHandler extends AbstractBooleanGETHandler {
    @Override
    protected boolean handle() {
        return Backend.isReplaying();
    }
}