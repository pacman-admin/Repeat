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
import utilities.json.IJsonable;

public class TaskGroupMessage implements IJsonable {

    public static final int UNKNOWN_INDEX = -1;

    private String name;
    private int index = UNKNOWN_INDEX;

    private TaskGroupMessage() {
    }

    private TaskGroupMessage(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static TaskGroupMessage of() {
        return new TaskGroupMessage();
    }

    public static TaskGroupMessage parseJSON(JsonNode node) {
        int index = UNKNOWN_INDEX;
        String name = "";

        if (node.isNumberValue("index")) {
            index = Integer.parseInt(node.getNumberValue("index"));
        }

        if (node.isStringValue("name")) {
            name = node.getStringValue("name");
        }

        return new TaskGroupMessage(name, index);
    }

    @Override
    public JsonRootNode jsonize() {
        return JsonNodeFactories.object(JsonNodeFactories.field("index", JsonNodeFactories.number(index)), JsonNodeFactories.field("name", JsonNodeFactories.string(name == null ? "" : name)));
    }

    public String getName() {
        return name;
    }

    public TaskGroupMessage setName(String name) {
        this.name = name;
        return this;
    }

    public boolean hasIndex() {
        return index != UNKNOWN_INDEX;
    }

    public int getIndex() {
        return index;
    }

    public TaskGroupMessage setIndex(int index) {
        this.index = index;
        return this;
    }
}
