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

public class TaskIdentifier implements IJsonable {
    private TaskMessage task;
    private TaskGroupMessage group;

    private TaskIdentifier() {
    }

    private TaskIdentifier(TaskMessage task, TaskGroupMessage group) {
        this.task = task;
        this.group = group;
    }

    public static TaskIdentifier of() {
        return new TaskIdentifier();
    }

    public static TaskIdentifier parseJSON(JsonNode node) {
        TaskMessage task = null;
        if (node.isObjectNode("task")) {
            task = TaskMessage.parseJSON(node.getNode("task"));
        }

        TaskGroupMessage group = null;
        if (node.isObjectNode("group")) {
            group = TaskGroupMessage.parseJSON(node.getNode("group"));
        }

        return new TaskIdentifier(task, group);
    }

    @Override
    public JsonRootNode jsonize() {
        Map<JsonStringNode, JsonNode> data = new HashMap<>();
        if (task != null) {
            data.put(JsonNodeFactories.string("task"), task.jsonize());
        }
        if (group != null) {
            data.put(JsonNodeFactories.string("group"), group.jsonize());
        }
        return JsonNodeFactories.object(data);
    }

    public TaskMessage getTask() {
        return task;
    }

    public TaskIdentifier setTask(TaskMessage task) {
        this.task = task;
        return this;
    }

    public TaskGroupMessage getGroup() {
        return group;
    }

    public TaskIdentifier setGroup(TaskGroupMessage group) {
        this.group = group;
        return this;
    }
}
