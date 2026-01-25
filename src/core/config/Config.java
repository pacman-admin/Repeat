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
import core.ipc.IPCServiceManager;
import core.keyChain.KeyChain;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.TaskGroupManager;
import utilities.FileUtility;
import utilities.ILoggable;
import utilities.json.JSONUtility;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static core.config.Constants.*;
import static core.userDefinedTask.TaskGroupManager.getTaskGroups;

public final class Config implements ILoggable {
    private static final HashMap<String, Parser> parsers = new HashMap<>();

    static {
        parsers.put("2.15", (root, config) -> {
            JsonNode globalSettings = root.getNode("global_settings");
            config.setExecuteOnKeyReleased(globalSettings.getBooleanValue("execute_on_key_released"));
            config.setUseClipboardToTypeString(globalSettings.getBooleanValue("use_clipboard_to_type_string"));
            config.setUseJavaAwtToGetMousePosition(globalSettings.getBooleanValue("use_java_awt_for_mouse_position"));
            config.setNativeHookDebugLevel(Level.parse(globalSettings.getNode("debug").getStringValue("level")));

            JsonNode globalHotkey = globalSettings.getNode("global_hotkey");
            String mouseGestureActivation = globalHotkey.getNumberValue("mouse_gesture_activation");
            config.setMOUSE_GESTURE(new KeyChain(Integer.parseInt(mouseGestureActivation)));
            config.setRECORD(KeyChain.parseJSON(globalHotkey.getArrayNode("record")));
            config.setREPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay")));
            config.setCOMPILED_REPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay_compiled")));

            List<JsonNode> ipcSettings = root.getArrayNode("ipc_settings");
            IPCServiceManager.parseJSON(ipcSettings);
            TaskGroupManager.COMPILER_FACTORY.parseJSON(root.getNode("compilers"));
            TaskGroupManager.parseJSON(root.getArrayNode("task_groups"));
        });
        parsers.put("3.0", (data, config) -> {
            JsonNode globalSettings = data.getNode("global_settings");
            config.setExecuteOnKeyReleased(globalSettings.getBooleanValue("execute_on_key_released"));
            config.setUseClipboardToTypeString(globalSettings.getBooleanValue("use_clipboard_to_type_string"));
            config.setUseJavaAwtToGetMousePosition(globalSettings.getBooleanValue("use_java_awt_for_mouse_position"));
            config.setNativeHookDebugLevel(Level.parse(globalSettings.getStringValue("debug_level")));


            JsonNode globalHotkey = globalSettings.getNode("global_hotkey");
            config.setMOUSE_GESTURE(KeyChain.parseJSON(globalHotkey.getArrayNode("mouse_gesture_activation")));
            config.setRECORD(KeyChain.parseJSON(globalHotkey.getArrayNode("record")));
            config.setREPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay")));
            config.setCOMPILED_REPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay_compiled")));

            IPCServiceManager.parseJSON(data.getArrayNode("ipc_settings"));
            TaskGroupManager.parseJSON(data.getArrayNode("task_groups"));
        });
    }

    private KeyChain RECORD;
    private KeyChain REPLAY;
    private KeyChain COMPILED_REPLAY;
    private KeyChain MOUSE_GESTURE;
    /**
     * If enabled will consider executing task on key released event. Otherwise will consider executing
     * task on key pressed event.
     */
    private boolean executeOnKeyReleased;
    private Level logLevel;
    // Instead of typing out the string, put the content into clipboard and paste it out.
    private boolean useClipboardToTypeString;
    // If enabled, will configure low level native hook to use Java AWT to get mouse position
    // instead of relying on native values returned by the hook.
    // Note that this is applicable for Windows.
    private boolean useJavaAwtToGetMousePosition;

    private Config() {
        logLevel = Level.INFO;
        executeOnKeyReleased = true;
        MOUSE_GESTURE = new KeyChain(KeyEvent.VK_F4);
        RECORD = new KeyChain(KeyEvent.VK_F7);
        REPLAY = new KeyChain(KeyEvent.VK_F8);
        COMPILED_REPLAY = new KeyChain(KeyEvent.VK_F9);
        initParsers();
    }

    public static Config loadFromFile() {
        Config c = new Config();
        File configFile = new File(CONFIG_FILE_NAME);
        if (FileUtility.fileExists(configFile)) {
            JsonRootNode root = JSONUtility.readJSON(configFile);
            String version = root.getStringValue("version");
            Parser parser = parsers.get(version);
            parser.parse(root, c);
        }
        TaskGroupManager.ensureHasAGroup();
        return c;
    }

    public void exportTasksConfig(File destination) {
        List<JsonNode> taskNodes = new ArrayList<>();
        for (TaskGroup group : getTaskGroups()) {
            taskNodes.add(group.jsonize());
        }

        JsonRootNode root = JsonNodeFactories.object(JsonNodeFactories.field("version", JsonNodeFactories.string(CURRENT_CONFIG_VERSION)), JsonNodeFactories.field("task_groups", JsonNodeFactories.array(taskNodes)));
        String fullPath = FileUtility.joinPath(destination.getAbsolutePath(), EXPORTED_CONFIG_FILE_NAME);
        JSONUtility.writeJson(root, new File(fullPath));
    }

    public void importTaskConfig() {
        File configFile = new File("tmp/" + EXPORTED_CONFIG_FILE_NAME);
        if (!configFile.isFile()) {
            getLogger().warning("Config file does not exist " + configFile.getAbsolutePath());
        }
        JsonRootNode root = JSONUtility.readJSON(configFile);
        if (root == null) {
            getLogger().warning("Unable to import config file " + configFile.getAbsolutePath());
            return;
        }
        TaskGroupManager.parseJSON(root.getArrayNode("task_groups"), ParsingMode.IMPORT_PARSING);
    }

    public boolean save() {
        List<JsonNode> taskNodes = new ArrayList<>();
        for (TaskGroup group : getTaskGroups()) {
            taskNodes.add(group.jsonize());
        }
        JsonRootNode r;
        r = JsonNodeFactories.object(
                JsonNodeFactories.field("version", JsonNodeFactories.string(CURRENT_CONFIG_VERSION)),
                JsonNodeFactories.field("global_settings",
                        JsonNodeFactories.object(JsonNodeFactories.field("debug_level", JsonNodeFactories.string(logLevel.toString())),
                                JsonNodeFactories.field("execute_on_key_released", JsonNodeFactories.booleanNode(executeOnKeyReleased)),
                                JsonNodeFactories.field("use_clipboard_to_type_string", JsonNodeFactories.booleanNode(useClipboardToTypeString)),
                                JsonNodeFactories.field("use_java_awt_for_mouse_position", JsonNodeFactories.booleanNode(useJavaAwtToGetMousePosition)),
                                JsonNodeFactories.field("global_hotkey", JsonNodeFactories.object(
                                        JsonNodeFactories.field("mouse_gesture_activation", MOUSE_GESTURE.jsonize()),
                                        JsonNodeFactories.field("record", RECORD.jsonize()),
                                        JsonNodeFactories.field("replay", REPLAY.jsonize()),
                                        JsonNodeFactories.field("replay_compiled", COMPILED_REPLAY.jsonize()))))),
                JsonNodeFactories.field("ipc_settings", IPCServiceManager.jsonize()),
                JsonNodeFactories.field("task_groups", JsonNodeFactories.array(taskNodes)));
        return JSONUtility.writeJson(r, new File(CONFIG_FILE_NAME));
    }

    public KeyChain getRECORD() {
        return RECORD;
    }

    public void setRECORD(KeyChain RECORD) {
        if (RECORD != null) {
            this.RECORD = RECORD;
        }
    }

    public KeyChain getMOUSE_GESTURE() {
        return MOUSE_GESTURE;
    }

    public void setMOUSE_GESTURE(KeyChain MOUSE_GESTURE) {
        this.MOUSE_GESTURE = MOUSE_GESTURE;
    }

    public KeyChain getREPLAY() {
        return REPLAY;
    }

    public void setREPLAY(KeyChain REPLAY) {
        if (REPLAY != null) {
            this.REPLAY = REPLAY;
        }
    }

    public KeyChain getCOMPILED_REPLAY() {
        return COMPILED_REPLAY;
    }

    public void setCOMPILED_REPLAY(KeyChain COMPILED_REPLAY) {
        if (COMPILED_REPLAY != null) {
            this.COMPILED_REPLAY = COMPILED_REPLAY;
        }
    }

    public boolean isUseTrayIcon() {
        return true;
    }

    public boolean isExecuteOnKeyReleased() {
        return executeOnKeyReleased;
    }

    public void setExecuteOnKeyReleased(boolean executeOnKeyReleased) {
        this.executeOnKeyReleased = executeOnKeyReleased;
    }

    public Level getNativeHookDebugLevel() {
        return logLevel;
    }

    public void setNativeHookDebugLevel(Level debugLevel) {
        logLevel = debugLevel;
    }

    public boolean isEnabledHaltingKeyPressed() {
        return true;
    }

    public boolean isUseClipboardToTypeString() {
        return useClipboardToTypeString;
    }

    public void setUseClipboardToTypeString(boolean useClipboardToTypeString) {
        this.useClipboardToTypeString = useClipboardToTypeString;
    }

    public boolean isRunTaskWithServerConfig() {
        return false;
    }

    public boolean isUseJavaAwtToGetMousePosition() {
        return useJavaAwtToGetMousePosition;
    }

    public void setUseJavaAwtToGetMousePosition(boolean useJavaToolsToGetMousePosition) {
        this.useJavaAwtToGetMousePosition = useJavaToolsToGetMousePosition;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(Config.class.getName());
    }

    private void initParsers() {

    }
}