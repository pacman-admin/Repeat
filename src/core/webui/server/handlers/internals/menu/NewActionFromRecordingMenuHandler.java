package core.webui.server.handlers.internals.menu;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractPOSTHandler;
import frontEnd.Backend;


public class NewActionFromRecordingMenuHandler extends AbstractPOSTHandler {
    public NewActionFromRecordingMenuHandler() {
        super("Could not create an action from recorded operations");
    }

    @Override
    protected String handleAsString(HttpExchange r) {
        Backend.createActionFromRecording();
        return "";
    }
}
