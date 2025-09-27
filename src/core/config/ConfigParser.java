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
import utilities.Pair;
import utilities.json.JSONUtility;

import java.util.logging.Level;
import java.util.logging.Logger;

abstract class ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(ConfigParser.class.getName());

    protected abstract String getVersion();

    protected abstract String getPreviousVersion();

    private JsonRootNode convertFromPreviousVersion(JsonRootNode previousVersion) {
        if (previousVersion != null && previousVersion.isStringValue("version")) {
            if (previousVersion.getStringValue("version").equals(getPreviousVersion())) {
                JsonRootNode output = internalConvertFromPreviousVersion(previousVersion);

                try {
                    JsonNode convertedVersion = JSONUtility.replaceChild(output, "version", JsonNodeFactories.string(getVersion()));
                    return convertedVersion.getRootNode();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Unable to modify version when converting versions " + getPreviousVersion() + " to " + getVersion(), e);
                    return null;
                }
            }
        }
        LOGGER.warning("Invalid previous version " + getPreviousVersion() + " cannot " + "be converted to this version " + getVersion() + ". Only accept previous version " + getPreviousVersion());
        return null;
    }

    protected abstract JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion);

    final boolean extractData(Config config, JsonRootNode data) {
        if (!sanityCheck(data)) {
            return false;
        }

        if (!Config.CURRENT_CONFIG_VERSION.equals(getVersion())) { // Then convert to latest version then parse
            Pair<ConfigParser, JsonRootNode> latest = convertDataToLatest(data);
            if (latest == null) {
                return false;
            }

            ConfigParser parser = latest.a();
            JsonRootNode latestData = latest.b();
            return parser.extractData(config, latestData);
        } else {
            return internalExtractData(config, data);
        }
    }

    private boolean sanityCheck(JsonRootNode data) {
        try {
            // Sanity check
            if (!data.getStringValue("version").equals(getVersion())) {
                LOGGER.warning("Invalid version " + data.getStringValue("version") + " with parser of version " + getVersion());
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot parse json", e);
            return false;
        }

        return true;
    }

    private Pair<ConfigParser, JsonRootNode> convertDataToLatest(JsonRootNode data) {
        if (Config.CURRENT_CONFIG_VERSION.equals(getVersion())) {
            return Pair.of(this, data);
        }

        // Then convert to latest version then parse
        LOGGER.info("Looking for next version " + getVersion());
        String currentVersion = getVersion();
        while (!currentVersion.equals(Config.CURRENT_CONFIG_VERSION)) {
            ConfigParser nextVersion = Config.getNextConfigParser(currentVersion);

            if (nextVersion == null) {
                LOGGER.warning("Unable to find the next version of current version " + currentVersion);
                return null;
            }

            try {
                data = nextVersion.convertFromPreviousVersion(data);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Unable to convert from version " + currentVersion, e);
                data = null;
            }

            if (data == null) {
                LOGGER.log(Level.WARNING, "Unable to convert to later version " + nextVersion.getVersion());
                return null;
            }
            currentVersion = nextVersion.getVersion();
        }

        ConfigParser parser = Config.getConfigParser(currentVersion);
        return Pair.of(parser, data);
    }

    boolean internalExtractData(Config config, JsonRootNode data) {
        throw new UnsupportedOperationException("Config version " + getVersion() + " does not support extracting data. " + "Convert to a later version.");
    }

    final boolean importData(Config config, JsonRootNode data) {
        return internalImportData(config, data);
    }

    protected abstract boolean internalImportData(Config config, JsonRootNode data);
}