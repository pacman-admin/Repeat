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

    private final List<String> enabledClients;

    protected AbstractRemoteRepeatsClientsConfig(List<String> remoteClientIds) {
        this.enabledClients = new ArrayList<>(remoteClientIds);
    }

    public static List<String> parseClientList(JsonNode node) {
        return node.getArrayNode().stream().map(JsonNode::getStringValue).collect(Collectors.toList());
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
