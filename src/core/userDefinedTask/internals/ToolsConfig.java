package core.userDefinedTask.internals;

import argo.jdom.JsonNode;
import core.config.AbstractRemoteRepeatsClientsConfig;

import java.util.List;

public class ToolsConfig extends AbstractRemoteRepeatsClientsConfig {

    public ToolsConfig(List<String> remoteClientIds) {
        super(remoteClientIds);
    }

    public static ToolsConfig parseJSON(JsonNode node) {
        return new ToolsConfig(AbstractRemoteRepeatsClientsConfig.parseClientList(node));
    }
}
