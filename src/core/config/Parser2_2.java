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
import utilities.json.JSONUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser2_2 extends ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(Parser2_2.class.getName());

    @Override
    protected String getVersion() {
        return "2.2";
    }

    @Override
    protected String getPreviousVersion() {
        return "2.1";
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        try {
            // Add classpath to java compiler as compiler specific args
            List<JsonNode> compilers = previousVersion.getArrayNode("compilers");

            List<JsonNode> replacement = new ArrayList<>();
            for (JsonNode compiler : compilers) {
                if (compiler.getStringValue("name").equals(Language.JAVA.toString())) {
                    replacement.add(JSONUtility.replaceChild(compiler, "compiler_specific_args", JsonNodeFactories.object(JsonNodeFactories.field("classpath", JsonNodeFactories.array()))));
                } else {
                    replacement.add(compiler);
                }
            }

            return JSONUtility.replaceChild(previousVersion, "compilers", JsonNodeFactories.array(replacement)).getRootNode();
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