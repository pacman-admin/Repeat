package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractPOSTHandler;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class MenuImportTaskActionHandler extends AbstractPOSTHandler {

    public MenuImportTaskActionHandler() {
        super("Could not import tasks.");
    }

    @Override
    protected String handle(HttpRequest r) {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(r);
        if (params == null) {
            throw new IllegalArgumentException("Request must not be empty.");
        }
        String path = params.get("path");
        if (path == null) {
            throw new IllegalArgumentException("A file path must be provided.");
        }
        if (!Files.isRegularFile(Paths.get(path))) {
            throw new IllegalArgumentException("Path '" + path + "' is not a file.");
        }
        backEndHolder.importTasks(new File(path));
        return "";
    }
}