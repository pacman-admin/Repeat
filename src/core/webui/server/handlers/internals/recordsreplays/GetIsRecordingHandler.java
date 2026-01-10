package core.webui.server.handlers.internals.recordsreplays;

import core.webui.server.handlers.AbstractBooleanGETHandler;
import frontEnd.Backend;

public class GetIsRecordingHandler extends AbstractBooleanGETHandler {
    @Override
    protected boolean handle() {
        return Backend.isRecording();
    }
}