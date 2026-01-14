package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractPOSTHandler;
import core.webui.server.handlers.renderedobjects.RenderedDebugLevel;
import core.webui.webcommon.HttpServerUtilities;
import main.Backend;
import org.apache.http.HttpRequest;
import utilities.NumberUtility;

import java.util.Map;
import java.util.logging.Level;

public final class MenuSetDebugLevelActionHandler extends AbstractPOSTHandler {

    public MenuSetDebugLevelActionHandler() {
        super("Could not set debug output level");
    }

    @Override
    protected String handle(HttpRequest request) {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
        if (params == null) {
            throw new IllegalArgumentException("Request empty");
        }

        String levelString = params.get("index");
        if (levelString == null || !NumberUtility.isNonNegativeInteger(levelString)) {
            throw new IllegalArgumentException("Debug Level must be non-negative integer.");
        }

        int levelIndex = Integer.parseInt(levelString);
        Level level = RenderedDebugLevel.LEVELS[Math.min(levelIndex, RenderedDebugLevel.LEVELS.length - 1)];
        Backend.changeDebugLevel(level);
        return "Successfully set debug output level";
    }
}