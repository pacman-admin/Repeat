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
package core.cli.messages;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import utilities.json.IJsonable;

import java.util.HashMap;
import java.util.Map;

public class TaskAddMessage implements IJsonable {

    private TaskIdentifier taskIdentifier;
    private String filePath;

    private TaskAddMessage() {
    }

    private TaskAddMessage(TaskIdentifier taskIdentifier, String filePath) {
        this.taskIdentifier = taskIdentifier;
        this.filePath = filePath;
    }

    public static TaskAddMessage of() {
        return new TaskAddMessage();
    }

    public static TaskAddMessage parseJSON(JsonNode node) {
        TaskIdentifier taskIdentifier = null;
        if (node.isObjectNode("task_identifier")) {
            taskIdentifier = TaskIdentifier.parseJSON(node.getNode("task_identifier"));
        }

        String filePath = "";
        if (node.isStringValue("file_path")) {
            filePath = node.getStringValue("file_path");
        }

        return new TaskAddMessage(taskIdentifier, filePath);
    }

    @Override
    public JsonRootNode jsonize() {
        Map<JsonStringNode, JsonNode> data = new HashMap<>();
        if (taskIdentifier != null) {
            data.put(JsonNodeFactories.string("task_identifier"), taskIdentifier.jsonize());
        }
        data.put(JsonNodeFactories.string("file_path"), JsonNodeFactories.string(filePath));

        return JsonNodeFactories.object(data);
    }

    public TaskIdentifier getTaskIdentifier() {
        return taskIdentifier;
    }

    public TaskAddMessage setTaskIdentifier(TaskIdentifier taskIdentifier) {
        this.taskIdentifier = taskIdentifier;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public TaskAddMessage setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }
}
