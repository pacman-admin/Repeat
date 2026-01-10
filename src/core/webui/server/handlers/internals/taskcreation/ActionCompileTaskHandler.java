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
package core.webui.server.handlers.internals.taskcreation;

import core.webui.server.handlers.AbstractPOSTHandler;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;
import org.apache.http.HttpRequest;

import java.nio.charset.StandardCharsets;

public class ActionCompileTaskHandler extends AbstractPOSTHandler {
    public ActionCompileTaskHandler() {
        super("Could not compile custom Action.");
    }

    @Override
    protected String handle(HttpRequest request) {
        byte[] data = HttpServerUtilities.getPostContent(request);
        if (data == null) throw new IllegalArgumentException("Unable to get POST request data.");
        String source = new String(data, StandardCharsets.UTF_8);
        if (source.isBlank()) throw new IllegalArgumentException("Nothing to compile.");
        boolean result = Backend.compileSourceAndSetCurrent(source, null);
        if (!result) {
            throw new RuntimeException("Source code could not be compiled, probably due to a syntax error.");
        }
        return "Successfully compiled custom action.";
    }
}