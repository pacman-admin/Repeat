package core.ipc.repeatServer;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is very similar to <class> UserDefinedAction </class>, except that this is meant to be
 * for direct communication between ipc java client and native server
 *
 * @author hptruong93
 *
 */
public record ClientTask(String id, String fileName) implements IJsonable {
    private static final Logger LOGGER = Logger.getLogger(ClientTask.class.getName());

    public static ClientTask of(String id, String fileName) {
        return new ClientTask(id, fileName);
    }

    public static ClientTask parseJSON(JsonNode node) {
        try {
            String id = node.getStringValue("id");
            String fileName = node.getStringValue("file_name");
            return new ClientTask(id, fileName);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse ClientTask.", e);
            return null;
        }
    }

    @Override
    public JsonRootNode jsonize() {
        return JsonNodeFactories.object(JsonNodeFactories.field("id", JsonNodeFactories.string(id)), JsonNodeFactories.field("file_name", JsonNodeFactories.string(fileName)));
    }
}