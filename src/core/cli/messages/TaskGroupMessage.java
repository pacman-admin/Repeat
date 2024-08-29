package core.cli.messages;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;

public final class TaskGroupMessage implements IJsonable {

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

    public int getIndex() {
        return index;
    }

    public TaskGroupMessage setIndex(int index) {
        this.index = index;
        return this;
    }
}
