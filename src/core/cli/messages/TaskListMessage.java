package core.cli.messages;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import utilities.json.IJsonable;

import java.util.HashMap;
import java.util.Map;

public final class TaskListMessage implements IJsonable {
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
