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
package core.languageHandler.compiler;

import argo.jdom.JsonNode;
import core.config.AbstractRemoteRepeatsClientsConfig;

import java.util.ArrayList;
import java.util.List;

public class RemoteRepeatsCompilerConfig extends AbstractRemoteRepeatsClientsConfig {
    RemoteRepeatsCompilerConfig(List<String> remoteClientIds) {
        super(remoteClientIds);
    }

    public static RemoteRepeatsCompilerConfig parseJSON(JsonNode node) {
        return new RemoteRepeatsCompilerConfig(AbstractRemoteRepeatsClientsConfig.parseClientList(node));
    }

    @Override
    public RemoteRepeatsCompilerConfig clone() {
        return new RemoteRepeatsCompilerConfig(new ArrayList<>(getClients()));
    }
}
