package core.config;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractRemoteRepeatsClientsConfig implements IJsonable {

    public static final String LOCAL_CLIENT = "local";

    private List<String> enabledClients;

    protected AbstractRemoteRepeatsClientsConfig(List<String> remoteClientIds) {
        this.enabledClients = new ArrayList<>(remoteClientIds);
    }

    public static List<String> parseClientList(JsonNode node) {
        return node.getArrayNode().stream().map(n -> n.getStringValue()).collect(Collectors.toList());
    }

    public final boolean hasLocal() {
        return enabledClients.contains(LOCAL_CLIENT);
    }

    public final boolean hasOnlyLocal() {
        return enabledClients.isEmpty() || (enabledClients.size() == 1 && enabledClients.get(0).equals(LOCAL_CLIENT));
    }

    public final List<String> getClients() {
        return enabledClients;
    }

    public final void setClients(List<String> remoteClientIds) {
        enabledClients.clear();
        enabledClients.addAll(remoteClientIds);
    }

    @Override
    public final JsonRootNode jsonize() {
        return JsonNodeFactories.array(JSONUtility.listToJson(enabledClients));
    }
}
