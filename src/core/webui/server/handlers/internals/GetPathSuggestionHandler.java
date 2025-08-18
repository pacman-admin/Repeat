/**
 * Copyright 2025 Langdon Staab
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        super("Could not get path suggestions!");
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