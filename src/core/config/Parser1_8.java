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
import core.languageHandler.Language;
import utilities.Function;
import utilities.json.JSONUtility;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser1_8 extends ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(Parser1_8.class.getName());

    @Override
    protected String getVersion() {
        return "1.8";
    }

    @Override
    protected String getPreviousVersion() {
        return "1.7";
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        try {
            JsonNode globalHotkeys = previousVersion.getNode("global_hotkey");
            JsonNode result = JSONUtility.removeChild(previousVersion, "global_hotkey");

            JsonNode globalSettings = JSONUtility.addChild(result.getNode("global_settings"), "global_hotkey", globalHotkeys);
            result = JSONUtility.replaceChild(result, "global_settings", globalSettings);
            result = JSONUtility.addChild(result, "ipc_settings", JsonNodeFactories.array(new Function<Language, JsonNode>() {
                @Override
                public JsonNode apply(Language l) {
                    return JsonNodeFactories.object(JsonNodeFactories.field("name", JsonNodeFactories.string(l.toString())), JsonNodeFactories.field("launch_at_startup", JsonNodeFactories.booleanNode(true)));
                }
            }.map(Language.values())));

            return result.getRootNode();
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