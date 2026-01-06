package core.webui.server.handlers.internals.recordsreplays;

import core.webui.server.handlers.AbstractBooleanGETHandler;

public class GetIsRecordingHandler extends AbstractBooleanGETHandler {
    @Override
    protected boolean handle() {
        return backEndHolder.isRecording();
    }
}