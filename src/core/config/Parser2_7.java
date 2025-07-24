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
import argo.jdom.JsonRootNode;
import core.userDefinedTask.TaskGroup;

public class Parser2_7 extends ConfigParser {

    @Override
    protected String getVersion() {
        return "2.7";
    }

    @Override
    protected String getPreviousVersion() {
        return "2.6";
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        return previousVersion; // Nothing to convert.
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