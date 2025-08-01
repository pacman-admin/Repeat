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
import core.controller.CoreConfig;
import core.ipc.IPCServiceManager;
import core.keyChain.KeyChain;
import core.languageHandler.compiler.DynamicCompilerManager;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.internals.ToolsConfig;
import frontEnd.MainBackEndHolder;
import utilities.FileUtility;
import utilities.ILoggable;
import utilities.json.JSONUtility;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config implements ILoggable {

    public static final String RELEASE_VERSION = "6.0.0";
    public static final int HALT_TASK = KeyEvent.VK_ESCAPE; // This should be hardcoded, and must not be changed
    protected static final String CONFIG_FILE_NAME = "config.json";
    public static final String EXPORTED_CONFIG_FILE_NAME = "exported_" + CONFIG_FILE_NAME;
    protected static final String CURRENT_CONFIG_VERSION = "2.15";
    private static final Level DEFAULT_NATIVE_HOOK_DEBUG_LEVEL = Level.INFO;
    private static final boolean DEFAULT_TRAY_ICON_USE = true;
    private static final List<ConfigParser> knownParsers;

    static {
        knownParsers = Arrays.asList(new Parser1_0(), new Parser1_1(), new Parser1_2(), new Parser1_3(), new Parser1_4(), new Parser1_5(), new Parser1_6(), new Parser1_7(), new Parser1_8(), new Parser1_9(), new Parser2_0(), new Parser2_1(), new Parser2_2(), new Parser2_3(), new Parser2_4(), new Parser2_5(), new Parser2_6(), new Parser2_7(), new Parser2_8(), new Parser2_9(), new Parser2_10(), new Parser2_11(), new Parser2_12(), new Parser2_13(), new Parser2_14(), new Parser2_15());
    }

    private final MainBackEndHolder backEnd;
    private DynamicCompilerManager compilerFactory;
    private ToolsConfig toolsConfig;
    private CoreConfig coreConfig;
    private KeyChain RECORD;
    private KeyChain REPLAY;
    private KeyChain COMPILED_REPLAY;
    private int mouseGestureActivationKey;
    private boolean useTrayIcon;
    private boolean enabledHaltingKeyPressed;
    /**
     * If enabled will consider executing task on key released event. Otherwise will consider executing
     * task on key pressed event.
     */
    private boolean executeOnKeyReleased;
    // Instead of typing out the string, put the content into clipboard and paste it out.
    private boolean useClipboardToTypeString;
    // If enabled, will run task with server config instead of asking for one.
    private boolean runTaskWithServerConfig;
    // If enabled, will configure low level native hook to use Java AWT to get mouse position
    // instead of relying on native values returned by the hook.
    // Note that this is applicable for Windows.
    private boolean useJavaAwtToGetMousePosition;
    private Level nativeHookDebugLevel;

    public Config(MainBackEndHolder backEnd) {
        this.backEnd = backEnd;
        useTrayIcon = DEFAULT_TRAY_ICON_USE;
        enabledHaltingKeyPressed = true;
        executeOnKeyReleased = true;
        nativeHookDebugLevel = DEFAULT_NATIVE_HOOK_DEBUG_LEVEL;
        mouseGestureActivationKey = KeyEvent.VK_BACK_QUOTE;
        RECORD = new KeyChain(KeyEvent.VK_F7);
        REPLAY = new KeyChain(KeyEvent.VK_F8);
        COMPILED_REPLAY = new KeyChain(KeyEvent.VK_F9);
    }

    protected static ConfigParser getConfigParser(String version) {
        for (ConfigParser parser : knownParsers) {
            if (parser.getVersion().equals(version)) {
                return parser;
            }
        }

        return null;
    }

    /**
     * Get config parser whose previous version is this version
     *
     * @param version the version to consider
     * @return the config parser whose previous version is this version
     */
    protected static ConfigParser getNextConfigParser(String version) {
        for (ConfigParser parser : knownParsers) {
            String previousVersion = parser.getPreviousVersion();
            if (previousVersion != null && previousVersion.equals(version)) {
                return parser;
            }
        }

        return null;
    }

    public DynamicCompilerManager getCompilerFactory() {
        return compilerFactory;
    }

    public void loadConfig(File file) {
        compilerFactory = new DynamicCompilerManager();
        if (file == null) System.out.println("Creating new config from default settings.");
        File configFile = file == null ? new File(CONFIG_FILE_NAME) : file;
        if (FileUtility.fileExists(configFile)) {
            JsonRootNode root = JSONUtility.readJSON(configFile);

            if (root == null||!root.isStringValue("version")) {
                getLogger().warning("Could not read config file!\nCreating new config file from default settings...");
                JOptionPane.showMessageDialog(null, "Could not read config file! Creating new config.");
                defaultExtract();
                writeConfig();
                return;
            }

            String version = root.getStringValue("version");
            ConfigParser parser = getConfigParser(version);
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
        toolsConfig = new ToolsConfig(Arrays.asList(ToolsConfig.LOCAL_CLIENT));
        coreConfig = new CoreConfig(Arrays.asList(ToolsConfig.LOCAL_CLIENT));
        List<TaskGroup> taskGroups = backEnd.getTaskGroups();
        backEnd.addTaskGroup(new TaskGroup("default"));
        backEnd.setCurrentTaskGroup(taskGroups.get(0));
    }

    public boolean writeConfig() {
        List<JsonNode> taskNodes = new ArrayList<>();
        for (TaskGroup group : backEnd.getTaskGroups()) {
            taskNodes.add(group.jsonize());
        }

        JsonRootNode root = JsonNodeFactories.object(JsonNodeFactories.field("version", JsonNodeFactories.string(CURRENT_CONFIG_VERSION)), JsonNodeFactories.field("global_settings", JsonNodeFactories.object(JsonNodeFactories.field("debug", JsonNodeFactories.object(JsonNodeFactories.field("level", JsonNodeFactories.string(nativeHookDebugLevel.toString())))), JsonNodeFactories.field("tray_icon_enabled", JsonNodeFactories.booleanNode(useTrayIcon)), JsonNodeFactories.field("enabled_halt_by_key", JsonNodeFactories.booleanNode(enabledHaltingKeyPressed)), JsonNodeFactories.field("execute_on_key_released", JsonNodeFactories.booleanNode(executeOnKeyReleased)), JsonNodeFactories.field("use_clipboard_to_type_string", JsonNodeFactories.booleanNode(useClipboardToTypeString)), JsonNodeFactories.field("run_task_with_server_config", JsonNodeFactories.booleanNode(runTaskWithServerConfig)), JsonNodeFactories.field("use_java_awt_for_mouse_position", JsonNodeFactories.booleanNode(useJavaAwtToGetMousePosition)), JsonNodeFactories.field("global_hotkey", JsonNodeFactories.object(JsonNodeFactories.field("mouse_gesture_activation", JsonNodeFactories.number(mouseGestureActivationKey)), JsonNodeFactories.field("record", RECORD.jsonize()), JsonNodeFactories.field("replay", REPLAY.jsonize()), JsonNodeFactories.field("replay_compiled", COMPILED_REPLAY.jsonize()))), JsonNodeFactories.field("tools_config", toolsConfig.jsonize()), JsonNodeFactories.field("core_config", coreConfig.jsonize()))), JsonNodeFactories.field("ipc_settings", IPCServiceManager.jsonize()), JsonNodeFactories.field("remote_repeats_clients", backEnd.getPeerServiceClientManager().jsonize()), JsonNodeFactories.field("compilers", compilerFactory.jsonize()), JsonNodeFactories.field("task_groups", JsonNodeFactories.array(taskNodes)));

        return JSONUtility.writeJson(root, new File(CONFIG_FILE_NAME));
    }

    public boolean exportTasksConfig(File destination) {
        List<JsonNode> taskNodes = new ArrayList<>();
        for (TaskGroup group : backEnd.getTaskGroups()) {
            taskNodes.add(group.jsonize());
        }

        JsonRootNode root = JsonNodeFactories.object(JsonNodeFactories.field("version", JsonNodeFactories.string(CURRENT_CONFIG_VERSION)), JsonNodeFactories.field("task_groups", JsonNodeFactories.array(taskNodes)));
        String fullPath = FileUtility.joinPath(destination.getAbsolutePath(), EXPORTED_CONFIG_FILE_NAME);
        return JSONUtility.writeJson(root, new File(fullPath));
    }

    public boolean importTaskConfig() {
        File configFile = new File(EXPORTED_CONFIG_FILE_NAME);
        if (!configFile.isFile()) {
            getLogger().warning("Config file does not exist " + configFile.getAbsolutePath());
            return false;
        }
        JsonRootNode root = JSONUtility.readJSON(configFile);
        if (root == null) {
            getLogger().warning("Unable to import config file " + configFile.getAbsolutePath());
            return false;
        }
        String version = root.getStringValue("version");
        ConfigParser parser = getConfigParser(version);
        if (parser == null) {
            getLogger().warning("Unknown version " + version);
            return false;
        }

        return parser.importData(this, root);
    }

    public ToolsConfig getToolsConfig() {
        return toolsConfig;
    }

    public void setToolsConfig(ToolsConfig toolsConfig) {
        this.toolsConfig = toolsConfig;
    }

    public CoreConfig getCoreConfig() {
        return coreConfig;
    }

    public void setCoreConfig(CoreConfig coreConfig) {
        this.coreConfig = coreConfig;
    }

    public int getMouseGestureActivationKey() {
        return mouseGestureActivationKey;
    }

    public void setMouseGestureActivationKey(int mouseGestureActivationKey) {
        this.mouseGestureActivationKey = mouseGestureActivationKey;
    }

    public KeyChain getRECORD() {
        return RECORD;
    }

    public void setRECORD(KeyChain RECORD) {
        if (RECORD != null) {
            this.RECORD = RECORD;
        }
    }

    public void setRECORD(int RECORD) {
        setRECORD(new KeyChain(Arrays.asList(RECORD)));
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
        return useTrayIcon;
    }

    public void setUseTrayIcon(boolean useTrayIcon) {
        this.useTrayIcon = useTrayIcon;
    }

    public boolean isExecuteOnKeyReleased() {
        return executeOnKeyReleased;
    }

    public void setExecuteOnKeyReleased(boolean executeOnKeyReleased) {
        this.executeOnKeyReleased = executeOnKeyReleased;
    }

    public Level getNativeHookDebugLevel() {
        return nativeHookDebugLevel;
    }

    public void setNativeHookDebugLevel(Level nativeHookDebugLevel) {
        this.nativeHookDebugLevel = nativeHookDebugLevel;
    }

    protected MainBackEndHolder getBackEnd() {
        return backEnd;
    }

    public boolean isEnabledHaltingKeyPressed() {
        return enabledHaltingKeyPressed;
    }

    public void setEnabledHaltingKeyPressed(boolean enabledHaltingKeyPressed) {
        this.enabledHaltingKeyPressed = enabledHaltingKeyPressed;
    }

    public boolean isUseClipboardToTypeString() {
        return useClipboardToTypeString;
    }

    public void setUseClipboardToTypeString(boolean useClipboardToTypeString) {
        this.useClipboardToTypeString = useClipboardToTypeString;
    }

    public boolean isRunTaskWithServerConfig() {
        return runTaskWithServerConfig;
    }

    public void setRunTaskWithServerConfig(boolean runTaskWithServerConfig) {
        this.runTaskWithServerConfig = runTaskWithServerConfig;
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
}
