package core.cli.messages;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import utilities.json.IJsonable;

import java.util.HashMap;
import java.util.Map;

public final class TaskExecuteMessage implements IJsonable {
    private TaskIdentifier taskIdentifier;

    private TaskExecuteMessage() {
    }

    private TaskExecuteMessage(TaskIdentifier taskIdentifier) {
        this.taskIdentifier = taskIdentifier;
    }

    public static TaskExecuteMessage of() {
        return new TaskExecuteMessage();
    }

    public static TaskExecuteMessage parseJSON(JsonNode node) {
        TaskIdentifier taskIdentifier = null;
        if (node.isObjectNode("task_identifier")) {
            taskIdentifier = TaskIdentifier.parseJSON(node.getNode("task_identifier"));
        }

        return new TaskExecuteMessage(taskIdentifier);
    }

    @Override
    public JsonRootNode jsonize() {
        Map<JsonStringNode, JsonNode> data = new HashMap<>();
        if (taskIdentifier != null) {
            data.put(JsonNodeFactories.string("task_identifier"), taskIdentifier.jsonize());
        }
        return JsonNodeFactories.object(data);
    }

    public TaskIdentifier getTaskIdentifier() {
        return taskIdentifier;
    }

    public TaskExecuteMessage setTaskIdentifier(TaskIdentifier taskIdentifier) {
        this.taskIdentifier = taskIdentifier;
        return this;
    }
}
