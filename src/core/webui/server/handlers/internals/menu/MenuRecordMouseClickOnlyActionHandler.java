package core.webui.server.handlers.internals.menu;

import core.recorder.Recorder;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import frontEnd.Backend;


public final class MenuRecordMouseClickOnlyActionHandler extends AbstractBooleanConfigHttpHandler {

    @Override
    public void handleAllowedRequestWithBackendAndValue(HttpExchange exchange, boolean value) {
        if (value) {
            Backend.recorder.setRecordMode(Recorder.MODE_MOUSE_CLICK_ONLY);
        } else {
            Backend.recorder.setRecordMode(Recorder.MODE_NORMAL);
        }
        emptySuccessResponse(exchange);
    }
}