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
package core.cli.server.handlers;

import argo.jdom.JsonNode;
import core.cli.messages.SharedVariablesSetMessage;
import core.cli.server.CliRpcCodec;
import core.userDefinedTask.SharedVariables;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;

public class SharedVariablesSetActionHandler extends SharedVariablesActionHandler {

    @Override
    protected Void handleSharedVariablesActionWithBackend(HttpAsyncExchange exchange, JsonNode requestData) throws IOException {
        SharedVariablesSetMessage message = SharedVariablesSetMessage.parseJSON(requestData);
        String namespace = message.getNamespace();
        if (namespace == null || namespace.isEmpty()) {
            namespace = SharedVariables.GLOBAL_NAMESPACE;
        }

        String variable = message.getVariable();
        if (variable == null || variable.isEmpty()) {
            return CliRpcCodec.prepareResponse(exchange, 400, "Empty variable name.");
        }

        String value = message.getValue();
        if (value != null) {
            SharedVariables.setVar(namespace, variable, value);
        }

        return CliRpcCodec.prepareResponse(exchange, 200, "");
    }
}
