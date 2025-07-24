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
import core.userDefinedTask.UsageStatistics;
import utilities.json.JSONUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser1_7 extends ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(Parser1_7.class.getName());

    @Override
    protected String getVersion() {
        return "1.7";
    }

    @Override
    protected String getPreviousVersion() {
        return "1.6";
    }

    private JsonNode internalTaskConversion(JsonNode tasks) {
        UsageStatistics reference = new UsageStatistics();

        List<JsonNode> converted = new ArrayList<>();
        for (JsonNode child : tasks.getArrayNode()) {
            converted.add(JSONUtility.addChild(child, "statistics", reference.jsonize()));
        }
        return JsonNodeFactories.array(converted);
    }

    private JsonNode internalTaskGroupConversion(JsonNode node) {
        List<JsonNode> converted = new ArrayList<>();
        for (JsonNode child : node.getArrayNode()) {
            JsonNode tasksNode = child.getNode("tasks");
            converted.add(JSONUtility.replaceChild(child, "tasks", internalTaskConversion(tasksNode)));
        }
        return JsonNodeFactories.array(converted);
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        try {
            JsonNode replacing = previousVersion.getNode("task_groups");
            JsonNode output = JSONUtility.replaceChild(previousVersion, "task_groups", internalTaskGroupConversion(replacing));
            return output.getRootNode();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
            return null;
        }
    }

    @Override
    protected boolean internalImportData(Config config, JsonRootNode root) {
        Parser1_8 parser = new Parser1_8();
        return parser.internalImportData(config, root);
    }
}