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

public class TaskListMessage implements IJsonable {
    private TaskGroupMessage group;

    private TaskListMessage() {
    }

    private TaskListMessage(TaskGroupMessage group) {
        this.group = group;
    }

    public static TaskListMessage of() {
        return new TaskListMessage();
    }

    public static TaskListMessage parseJSON(JsonNode node) {
        TaskGroupMessage group = null;
        if (node.isObjectNode("group")) {
            group = TaskGroupMessage.parseJSON(node.getNode("group"));
        }

        return new TaskListMessage(group);
    }

    @Override
    public JsonRootNode jsonize() {
        Map<JsonStringNode, JsonNode> data = new HashMap<>();
        if (group != null) {
            data.put(JsonNodeFactories.string("group"), group.jsonize());
        }
        return JsonNodeFactories.object(data);
    }

    public TaskGroupMessage getGroup() {
        return group;
    }

    public TaskListMessage setGroup(TaskGroupMessage group) {
        this.group = group;
        return this;
    }
}
