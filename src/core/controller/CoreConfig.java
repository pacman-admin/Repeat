package core.controller;

import argo.jdom.JsonNode;
import core.config.AbstractRemoteRepeatsClientsConfig;

import java.util.List;

public final class CoreConfig extends AbstractRemoteRepeatsClientsConfig {

    public CoreConfig(List<String> remoteClientIds) {
        super(remoteClientIds);
    }

    public static CoreConfig parseJSON(JsonNode node) {
        return new CoreConfig(AbstractRemoteRepeatsClientsConfig.parseClientList(node));
    }
}
