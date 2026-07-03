package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractPOSTHandler;
import frontEnd.Backend;
import org.apache.http.HttpRequest;

public class NewActionFromRecordingMenuHandler extends AbstractPOSTHandler {
    public NewActionFromRecordingMenuHandler() {
        super("Could not create an action from recorded operations");
    }

    @Override
    protected String handle(HttpRequest r) {
        Backend.createActionFromRecording();
        return "";
    }
}
