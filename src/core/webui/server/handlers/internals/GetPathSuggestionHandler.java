/**
 * Copyright 2025 Langdon Staab
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Langdon Staab
 * @author HP Truong
 */
package core.webui.server.handlers.internals;

import core.webui.server.handlers.AbstractComplexGETHandler;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import utilities.json.JSONUtility;
import utilities.json.Jsonizer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetPathSuggestionHandler extends AbstractComplexGETHandler {
    public GetPathSuggestionHandler() {
        super(AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    protected String handle(Map<String, String> params) {
        if (params == null) {
            throw new IllegalArgumentException("No parameters supplied!");
        }
        String path = params.get("path");
        if (path == null) {
            throw new IllegalArgumentException("A path must be provided.");
        }
        if (path.isBlank()) path = ".";
        Path p = Paths.get(path);
        if (!Files.exists(p)) {
            return paths();
        }
        if (Files.isRegularFile(p)) {
            return paths(p.toAbsolutePath().toString());
        }
        if (Files.isDirectory(p)) {
            File[] files = p.toFile().listFiles();
            List<String> suggested = Arrays.asList(files).stream().map(File::getAbsolutePath).collect(Collectors.toList());
            return paths(suggested);
        }
        return paths();
    }

    private String paths(String... paths) {
        return paths(Arrays.asList(paths));
    }

    private String paths(Iterable<String> paths) {
        return JSONUtility.jsonToString(Jsonizer.jsonize(SuggestedPaths.of(paths)).getRootNode());
    }

    private static class SuggestedPaths {
        private List<String> paths;

        private static SuggestedPaths of(Iterable<String> paths) {
            SuggestedPaths output = new SuggestedPaths();
            output.paths = new ArrayList<>();
            paths.forEach(output.paths::add);
            return output;
        }
    }
}