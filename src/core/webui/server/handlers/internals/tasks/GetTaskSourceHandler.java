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
package core.webui.server.handlers.internals.tasks;

import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractComplexGETHandler;

import java.util.Map;

public class GetTaskSourceHandler extends AbstractComplexGETHandler {

    public GetTaskSourceHandler() {
        super("Could not get source code for current Action.");
    }

    @Override
    protected String handle(Map<String, String> params) {

        if (params == null) throw new IllegalArgumentException("Params must not be null.");

        String id = params.get("id");
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Task ID is empty or not provided.");

        String timestampString = params.get("timestamp");
        if (timestampString == null || timestampString.isBlank())
            throw new IllegalArgumentException("Timestamp is empty or not provided.");

        UserDefinedAction action = backEndHolder.getTask(id);
        if (action == null) throw new NullPointerException("Could not find Action with ID: " + id);

        Long timestamp = Long.parseLong(timestampString);
        String sourceCode = backEndHolder.getSourceForTask(action, timestamp);
        if (sourceCode == null) throw new NullPointerException("Could not find source code for Action with ID: " + id);

        return sourceCode;
    }
}