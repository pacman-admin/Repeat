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
import utilities.json.JSONUtility;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser1_6 extends ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(Parser1_6.class.getName());

    @Override
    protected String getVersion() {
        return "1.6";
    }

    @Override
    protected String getPreviousVersion() {
        return "1.5";
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        try {
            JsonNode globalSettings = JsonNodeFactories.object(JsonNodeFactories.field("debug", JsonNodeFactories.object(JsonNodeFactories.field("level", JsonNodeFactories.string(Level.WARNING.toString())))), JsonNodeFactories.field("tray_icon_enabled", JsonNodeFactories.booleanNode(true)), JsonNodeFactories.field("enabled_halt_by_key", JsonNodeFactories.booleanNode(true)));

            JsonNode newNode = JSONUtility.addChild(previousVersion, "global_settings", globalSettings);
            return newNode.getRootNode();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
            return null;
        }
    }

    @Override
    protected boolean internalImportData(Config config, JsonRootNode data) {
        LOGGER.warning("Unsupported import data at version " + getVersion());
        return false;
    }
}