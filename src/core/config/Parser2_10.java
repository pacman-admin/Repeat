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
import core.userDefinedTask.TaskGroup;
import utilities.json.JSONUtility;

import java.util.ArrayList;
import java.util.List;

public class Parser2_10 extends ConfigParser {

    @Override
    protected String getVersion() {
        return "2.10";
    }

    @Override
    protected String getPreviousVersion() {
        return "2.9";
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        JsonNode taskGroupsNode = previousVersion.getNode("task_groups");
        List<JsonNode> groups = taskGroupsNode.getArrayNode();
        List<JsonNode> newGroups = new ArrayList<>();
        for (JsonNode groupNode : groups) {
            List<JsonNode> tasks = groupNode.getArrayNode("tasks");
            List<JsonNode> newTasks = new ArrayList<>();
            for (JsonNode taskNode : tasks) {
                JsonNode activation = taskNode.getNode("activation");
                activation = JSONUtility.addChild(activation, "variables", JsonNodeFactories.array());
                taskNode = JSONUtility.replaceChild(taskNode, "activation", activation);
                newTasks.add(taskNode);
            }

            groupNode = JSONUtility.replaceChild(groupNode, "tasks", JsonNodeFactories.array(newTasks));
            newGroups.add(groupNode);
        }

        return JSONUtility.replaceChild(previousVersion, "task_groups", JsonNodeFactories.array(newGroups)).getRootNode();
    }

    @Override
    protected boolean internalImportData(Config config, JsonRootNode root) {
        boolean result = true;

        for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
            TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode, ConfigParsingMode.IMPORT_PARSING);
            result &= taskGroup != null;
            if (taskGroup != null) {
                result &= config.getBackEnd().addPopulatedTaskGroup(taskGroup);
            }
        }
        return result;
    }
}