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

import argo.jdom.JsonRootNode;
import utilities.FileUtility;
import utilities.json.JSONUtility;

import javax.swing.*;
import java.io.File;

public class CliConfig {
    public static final int DEFAULT_SERVER_PORT = 65432;

    private int serverPort;

    public CliConfig() {
        this.serverPort = DEFAULT_SERVER_PORT;
    }

    public void loadConfig(File file) {
        File configFile = file == null ? new File(Config.CONFIG_FILE_NAME) : file;
        if (FileUtility.fileExists(configFile)) {
            JsonRootNode root = JSONUtility.readJSON(configFile);

            if (root == null) {
                JOptionPane.showMessageDialog(null, "Config file is not in json format");
                return;
            } else if (!root.isStringValue("version")) {
                JOptionPane.showMessageDialog(null, "Config file is in unknown version");
                return;
            }

            String version = root.getStringValue("version");
            ConfigParser parser = Config.getConfigParser(version);
            boolean foundVersion = parser != null;
            boolean extractResult = false;
            if (foundVersion) {
                extractResult = parser.extractData(this, root);
            }

            if (!foundVersion) {
                JOptionPane.showMessageDialog(null, "Config file is in unknown version " + version);
                defaultExtract();
            }

            if (!extractResult) {
                JOptionPane.showMessageDialog(null, "Cannot extract result with version " + version);
                defaultExtract();
            }
        } else {
            defaultExtract();
        }
    }

    private void defaultExtract() {
        // Nothing to do here.
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
