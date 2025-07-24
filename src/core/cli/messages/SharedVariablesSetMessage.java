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
package core.cli.messages;

import argo.jdom.JsonNode;
import utilities.json.AutoJsonable;
import utilities.json.Jsonizer;

public class SharedVariablesSetMessage extends AutoJsonable {

    private String namespace;
    private String variable;
    private String value;

    public static SharedVariablesSetMessage parseJSON(JsonNode node) {
        SharedVariablesSetMessage output = new SharedVariablesSetMessage();
        return Jsonizer.parse(node, output) ? output : null;
    }

    public static SharedVariablesSetMessage of() {
        return new SharedVariablesSetMessage();
    }

    public String getNamespace() {
        return namespace;
    }

    public SharedVariablesSetMessage setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getVariable() {
        return variable;
    }

    public SharedVariablesSetMessage setVariable(String variable) {
        this.variable = variable;
        return this;
    }

    public String getValue() {
        return value;
    }

    public SharedVariablesSetMessage setValue(String value) {
        this.value = value;
        return this;
    }
}
