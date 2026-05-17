package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractPOSTHandler;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;
import org.apache.http.HttpRequest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public final class MenuExportTaskActionHandler extends AbstractPOSTHandler {

    public MenuExportTaskActionHandler() {
        super("Could not export tasks.");
    }

    @Override
    protected String handle(HttpRequest r) {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(r);
        if (params == null) {
            throw new IllegalArgumentException("Failed to get POST parameters.");
        }
        String path = params.get("path");
        if (path == null) {
            throw new IllegalArgumentException("Path must be provided.");
        }
        if (!Files.isDirectory(Paths.get(path))) {
            throw new IllegalArgumentException("Path is not a directory.");
        }

        Backend.exportTasks(new File(path));
        return "Success!";
    }
}