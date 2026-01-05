package core.webui.server.handlers.internals.menu;

import core.recorder.Recorder;
import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public class MenuRecordMouseClickOnlyActionHandler extends AbstractBooleanConfigHttpHandler {

    @Override
    protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) {
        if (value) {
            backEndHolder.getRecorder().setRecordMode(Recorder.MODE_MOUSE_CLICK_ONLY);
        } else {
            backEndHolder.getRecorder().setRecordMode(Recorder.MODE_NORMAL);
        }
        return emptySuccessResponse(exchange);
    }
}