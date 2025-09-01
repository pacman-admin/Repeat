/**
 * Copyright 2025 Langdon Staab
 * <p>v
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

import core.webui.server.handlers.AbstractPOSTHandler;
import org.apache.http.HttpRequest;

public class ActionClearLogHandler extends AbstractPOSTHandler {

    public ActionClearLogHandler() {
        super("Could not clear logs!");
    }

    @Override
    protected String handle(HttpRequest ignored) {
        backEndHolder.clearLogs();
        return "Cleared logs.";
    }
}