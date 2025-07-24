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
import java.util.UUID;

public class Parser2_9 extends ConfigParser {

    @Override
    protected String getVersion() {
        return "2.9";
    }

    @Override
    protected String getPreviousVersion() {
        return "2.8";
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        JsonNode globalConfig = previousVersion.getNode("global_settings");
        globalConfig = JSONUtility.addChild(globalConfig, "tools_config", JsonNodeFactories.array(JsonNodeFactories.string("local"))).getRootNode();
        globalConfig = JSONUtility.addChild(globalConfig, "core_config", JsonNodeFactories.array(JsonNodeFactories.string("local"))).getRootNode();
        previousVersion = JSONUtility.replaceChild(previousVersion, "global_settings", globalConfig).getRootNode();
        previousVersion = JSONUtility.addChild(previousVersion, "remote_repeats_clients", JsonNodeFactories.object(JsonNodeFactories.field(JsonNodeFactories.string("clients"), JsonNodeFactories.array()))).getRootNode();

        JsonNode compilers = previousVersion.getNode("compilers");
        compilers = JsonNodeFactories.object(JsonNodeFactories.field("local_compilers", compilers), JsonNodeFactories.field("remote_repeats_compilers", JsonNodeFactories.array(JsonNodeFactories.string("local"))));
        previousVersion = JSONUtility.replaceChild(previousVersion, "compilers", compilers).getRootNode();

        List<JsonNode> groups = previousVersion.getArrayNode("task_groups");
        List<JsonNode> newGroups = new ArrayList<>();
        for (JsonNode group : groups) {
            List<JsonNode> tasks = group.getArrayNode("tasks");
            List<JsonNode> newTasks = new ArrayList<>();
            for (JsonNode task : tasks) {
                JsonNode taskWithId = JSONUtility.addChild(task, "action_id", JsonNodeFactories.string(UUID.randomUUID().toString()));
                newTasks.add(taskWithId);
            }

            JsonNode newGroup = JSONUtility.replaceChild(group, "tasks", JsonNodeFactories.array(newTasks));
            newGroup = JSONUtility.addChild(newGroup, "group_id", JsonNodeFactories.string(UUID.randomUUID().toString()));
            newGroups.add(newGroup);
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