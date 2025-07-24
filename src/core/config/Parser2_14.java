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
import java.util.stream.Collectors;

public class Parser2_14 extends ConfigParser {

    @Override
    protected String getVersion() {
        return "2.14";
    }

    @Override
    protected String getPreviousVersion() {
        return "2.13";
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        JsonNode taskGroups = previousVersion.getNode("task_groups");
        return JSONUtility.replaceChild(previousVersion, "task_groups", convertTaskGroups(taskGroups)).getRootNode();
    }

    private JsonNode convertTaskGroups(JsonNode node) {
        List<JsonNode> groups = node.getArrayNode();
        List<JsonNode> convertedGroups = new ArrayList<>();
        for (JsonNode group : groups) {
            List<JsonNode> convertedTasks = group.getArrayNode("tasks").stream().map(task -> convertTask(task)).collect(Collectors.toList());
            convertedGroups.add(JSONUtility.replaceChild(group, "tasks", JsonNodeFactories.array(convertedTasks)));
        }

        return JsonNodeFactories.array(convertedGroups);
    }

    private JsonNode convertTask(JsonNode node) {
        String currentPath = node.getStringValue("source_path");
        JsonNode currentSource = JsonNodeFactories.object(JsonNodeFactories.field("path", JsonNodeFactories.string(currentPath)), JsonNodeFactories.field("created_time", JsonNodeFactories.number(System.currentTimeMillis())));

        return JSONUtility.addChild(node, "source_history", JsonNodeFactories.object(JsonNodeFactories.field("entries", JsonNodeFactories.array(currentSource))));
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