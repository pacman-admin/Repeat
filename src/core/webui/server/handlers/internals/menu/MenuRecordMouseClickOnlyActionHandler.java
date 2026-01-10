package core.webui.server.handlers.internals.menu;

import core.recorder.Recorder;
import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import frontEnd.Backend;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public class MenuRecordMouseClickOnlyActionHandler extends AbstractBooleanConfigHttpHandler {

    @Override
    protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) {
        if (value) {
            Backend.recorder.setRecordMode(Recorder.MODE_MOUSE_CLICK_ONLY);
        } else {
            Backend.recorder.setRecordMode(Recorder.MODE_NORMAL);
        }
        return emptySuccessResponse(exchange);
    }
}