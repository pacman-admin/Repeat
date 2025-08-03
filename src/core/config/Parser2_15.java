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
import core.cli.server.CliServer;
import core.controller.CoreConfig;
import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;
import core.keyChain.KeyChain;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.internals.ToolsConfig;
import utilities.json.JSONUtility;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Parser2_15 extends ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(Parser2_15.class.getName());

    @Override
    protected String getVersion() {
        return "2.15";
    }

    @Override
    protected String getPreviousVersion() {
        return "2.14";
    }

    @Override
    protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
        JsonNode result = previousVersion.getNode();

        if (result.isArrayNode("ipc_settings")) {
            List<JsonNode> ipcSettings = result.getArrayNode("ipc_settings");
            List<JsonNode> newIpcSettings = ipcSettings.stream().filter(x -> !x.getStringValue("name").equals("scala")).collect(Collectors.toList());
            result = JSONUtility.replaceChild(previousVersion, "ipc_settings", JsonNodeFactories.array(newIpcSettings));
        }

        if (result.isNode("compilers")) {
            JsonNode compilerNode = result.getNode("compilers");
            List<JsonNode> localCompilers = compilerNode.getArrayNode("local_compilers");
            List<JsonNode> newLocalCompilers = localCompilers.stream().filter(x -> !x.getStringValue("name").equals("scala")).collect(Collectors.toList());
            JsonNode newCompilerNode = JSONUtility.replaceChild(compilerNode, "local_compilers", JsonNodeFactories.array(newLocalCompilers));
            result = JSONUtility.replaceChild(result, "compilers", newCompilerNode);
        }

        return result.getRootNode();
    }

    @Override
    protected boolean internalExtractData(Config config, JsonRootNode root) {
        try {
            JsonNode globalSettings = root.getNode("global_settings");
            config.setUseTrayIcon(globalSettings.getBooleanValue("tray_icon_enabled"));
            config.setEnabledHaltingKeyPressed(globalSettings.getBooleanValue("enabled_halt_by_key"));
            config.setExecuteOnKeyReleased(globalSettings.getBooleanValue("execute_on_key_released"));
            config.setUseClipboardToTypeString(globalSettings.getBooleanValue("use_clipboard_to_type_string"));
            config.setRunTaskWithServerConfig(globalSettings.getBooleanValue("run_task_with_server_config"));
            config.setUseJavaAwtToGetMousePosition(globalSettings.getBooleanValue("use_java_awt_for_mouse_position"));

            config.setNativeHookDebugLevel(Level.parse(globalSettings.getNode("debug").getStringValue("level")));

            JsonNode globalHotkey = globalSettings.getNode("global_hotkey");

            String mouseGestureActivation = globalHotkey.getNumberValue("mouse_gesture_activation");
            config.setMouseGestureActivationKey(Integer.parseInt(mouseGestureActivation));
            config.setRECORD(KeyChain.parseJSON(globalHotkey.getArrayNode("record")));
            config.setREPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay")));
            config.setCOMPILED_REPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay_compiled")));

            JsonNode toolsConfigNode = globalSettings.getNode("tools_config");
            ToolsConfig toolsConfig = ToolsConfig.parseJSON(toolsConfigNode);
            config.setToolsConfig(toolsConfig);

            JsonNode coreConfigNode = globalSettings.getNode("core_config");
            CoreConfig coreConfig = CoreConfig.parseJSON(coreConfigNode);
            config.setCoreConfig(coreConfig);

            JsonNode peerClients = root.getNode("remote_repeats_clients");
            RepeatsPeerServiceClientManager repeatsPeerServiceClientManager = RepeatsPeerServiceClientManager.parseJSON(peerClients);
            config.getBackEnd().getPeerServiceClientManager().updateClients(repeatsPeerServiceClientManager.getClients());

            List<JsonNode> ipcSettings = root.getArrayNode("ipc_settings");
            if (!IPCServiceManager.parseJSON(ipcSettings)) {
                LOGGER.log(Level.WARNING, "IPC Service Manager failed to parse JSON metadata");
            }

            if (!config.getCompilerFactory().parseJSON(root.getNode("compilers"))) {
                LOGGER.log(Level.WARNING, "Dynamic Compiler Manager failed to parse JSON metadata");
            }

            config.getBackEnd().clearTaskGroup();
            for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
                TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode, ConfigParsingMode.DEFAULT);
                if (taskGroup != null) {
                    config.getBackEnd().addTaskGroup(taskGroup);
                }
            }

            if (config.getBackEnd().getTaskGroups().isEmpty()) {
                config.getBackEnd().addTaskGroup(new TaskGroup("default"));
            }
            config.getBackEnd().setCurrentTaskGroup(config.getBackEnd().getTaskGroups().get(0));
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to parse json", e);
            return false;
        }
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

    @Override
    protected boolean internalExtractData(CliConfig config, JsonRootNode root) {
        try {
            List<JsonNode> ipcSettings = root.getArrayNode("ipc_settings");
            if (!IPCServiceManager.parseJSON(ipcSettings)) {
                LOGGER.log(Level.WARNING, "IPC Service Manager failed to parse JSON metadata");
            }

            //CliServer cliServer = (CliServer) IPCServiceManager.getIPCService(IPCServiceName.CLI_SERVER);
            //config.setServerPort(cliServer.getPort());
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to parse json", e);
            return false;
        }
    }
}