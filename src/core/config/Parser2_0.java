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

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser2_0 extends ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(Parser2_0.class.getName());

    @Override
    protected String getVersion() {
        return "2.0";
    }

    @Override
    protected String getPreviousVersion() {
        return "1.9";
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        try {
            // Add mouse gesture activation to global hotkey
            JsonNode globalSettings = previousVersion.getNode("global_settings");
            JsonNode globalHotkey = globalSettings.getNode("global_hotkey");

            globalHotkey = JSONUtility.addChild(globalHotkey, "mouse_gesture_activation", JsonNodeFactories.number(KeyEvent.VK_SHIFT));

            globalSettings = JSONUtility.replaceChild(globalSettings, "global_hotkey", globalHotkey);

            JsonNode output = JSONUtility.replaceChild(previousVersion, "global_settings", globalSettings).getRootNode();

            // Modify hotkey into activation
            List<JsonNode> taskGroups = output.getArrayNode("task_groups");
            List<JsonNode> convertedTaskGroup = new ArrayList<>();
            for (JsonNode group : taskGroups) {
                List<JsonNode> tasks = group.getArrayNode("tasks");
                List<JsonNode> convertedTasks = new ArrayList<>();
                for (JsonNode task : tasks) {
                    JsonNode hotkey = task.getNode("hotkey");
                    JsonNode activation = JsonNodeFactories.object(JsonNodeFactories.field("hotkey", hotkey), JsonNodeFactories.field("mouse_gesture", JsonNodeFactories.array()));

                    task = JSONUtility.addChild(task, "activation", activation);
                    convertedTasks.add(task);
                }


                group = JSONUtility.replaceChild(group, "tasks", JsonNodeFactories.array(convertedTasks));
                convertedTaskGroup.add(group);
            }
            output = JSONUtility.replaceChild(output, "task_groups", JsonNodeFactories.array(convertedTaskGroup));

            return output.getRootNode();
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