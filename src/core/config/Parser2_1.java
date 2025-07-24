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
import utilities.Function;
import utilities.json.JSONUtility;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser2_1 extends ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(Parser2_1.class.getName());

    @Override
    protected String getVersion() {
        return "2.1";
    }

    @Override
    protected String getPreviousVersion() {
        return "2.0";
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        try {
            // Move ipc settings into config
            List<JsonNode> ipcSettingNodes = previousVersion.getArrayNode("ipc_settings");
            List<JsonNode> converted = new Function<JsonNode, JsonNode>() {
                @Override
                public JsonNode apply(JsonNode d) {
                    String name = d.getStringValue("name");
                    boolean launchAtStartup = d.getBooleanValue("launch_at_startup");

                    return JsonNodeFactories.object(JsonNodeFactories.field("name", JsonNodeFactories.string(name)), JsonNodeFactories.field("config", JsonNodeFactories.object(JsonNodeFactories.field("launch_at_startup", JsonNodeFactories.booleanNode(launchAtStartup)))));
                }
            }.map(ipcSettingNodes);

            return JSONUtility.replaceChild(previousVersion, "ipc_settings", JsonNodeFactories.array(converted)).getRootNode();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
            return null;
        }
    }

    @Override
    protected boolean internalImportData(Config config, JsonRootNode root) {
        ConfigParser parser = Config.getNextConfigParser(getVersion());
        return parser.internalImportData(config, root);
    }
}