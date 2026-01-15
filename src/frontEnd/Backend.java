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
package frontEnd;

import core.config.Config;
import core.controller.Core;
import core.ipc.IPCServiceManager;
import core.ipc.repeatServer.processors.TaskProcessorManager;
import core.keyChain.ActionInvoker;
import core.keyChain.managers.GlobalEventsManager;
import core.languageHandler.Language;
import core.languageHandler.compiler.AbstractNativeCompiler;
import core.languageHandler.compiler.DynamicCompilationResult;
import core.languageHandler.compiler.DynamicCompilerOutput;
import core.recorder.Recorder;
import core.recorder.ReplayConfig;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.TaskGroupManager;
import core.userDefinedTask.TaskSourceManager;
import core.userDefinedTask.UserDefinedAction;
import core.userDefinedTask.internals.ActionExecutor;
import core.userDefinedTask.internals.RunActionConfig;
import core.userDefinedTask.internals.TaskSourceHistoryEntry;
import globalListener.GlobalListenerHookController;
import staticResources.BootStrapResources;
import utilities.*;
import utilities.Desktop;
import utilities.logging.CompositeOutputStream;
import utilities.logging.LogHolder;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;

import static core.userDefinedTask.TaskGroupManager.*;

@SuppressWarnings("DanglingJavadoc")
public final class Backend {
    public static final Config config = Config.loadFromFile();
    public static final ActionExecutor actionExecutor = new ActionExecutor(Core.local(config));
    public static final GlobalEventsManager keysManager = new GlobalEventsManager(config, actionExecutor);
    public static final Recorder recorder = new Recorder(Core.local(config));
    private static final LogHolder logHolder = new LogHolder();
    private static final Logger LOGGER = Logger.getLogger(Backend.class.getName());
    public static ReplayConfig replayConfig;
    private static Language compilingLanguage;
    private static RunActionConfig runActionConfig;
    private static boolean isRecording, isReplaying, isRunningCompiledTask;
    private static final UserDefinedAction switchRecord = UserDefinedAction.of(Backend::switchRecord);
    private static final UserDefinedAction switchReplay = UserDefinedAction.of(Backend::switchReplay);
    private static File currentTempFile;
    private static MinimizedFrame trayIcon;
    private static Thread compiledExecutor;
    private static UserDefinedAction customFunction;
    private static final UserDefinedAction switchReplayCompiled = UserDefinedAction.of(Backend::switchRunningCompiledAction);

    private Backend() {
        //This class is uninstantiable
    }

    static void init() {
        replayConfig = ReplayConfig.of();
        compilingLanguage = Language.MANUAL_BUILD;
        runActionConfig = RunActionConfig.of();
        TaskProcessorManager.setProcessorIdentifyCallback(new Function<>() {
            @Override
            public Void apply(Language language) {
                Backend.recompiledNativeTasks(language);
                return null;
            }
        });
        if (!SystemTray.isSupported()) {
            LOGGER.warning("System tray is not supported.");
        }
        TaskGroupManager.ensureHasAGroup();
        try {
            LOGGER.info("Adding tray icon...");
            trayIcon = new MinimizedFrame(BootStrapResources.TRAY_IMAGE);
            LOGGER.info("Tray Icon added");
        } catch (Exception e) {
            LOGGER.warning("Could not add tray icon!\n" + e.getMessage());
        }
        trayIcon.add();
    }

    public static void editSource(String code) {
        LOGGER.info("Opening source code in editor...");
        try {
            File f = File.createTempFile("source", compilingLanguage == Language.JAVA ? ".java" : ".txt");
            FileUtility.writeToFile(code, f, false);
            if (Desktop.openFile(f)) currentTempFile = f;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String reloadSource() {
        LOGGER.info("Reloading edits...");
        if (currentTempFile == null) throw new RuntimeException("Source code was never opened for editing.");
        return Objects.requireNonNull(FileUtility.readFromFile(currentTempFile)).toString();
    }

    static void initializeLogging() {
        // Change stdout and stderr to also copy content to the logHolder.
        System.setOut(new PrintStream(CompositeOutputStream.of(logHolder, System.out)));
        System.setErr(new PrintStream(CompositeOutputStream.of(logHolder, System.err)));

        // Once we've updated stdout and stderr, we need to re-register the ConsoleHandler of the root
        // logger because it was only logging to the old stderr which we just changed above.
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            if (handler.getClass().getName().equals(ConsoleHandler.class.getName())) {
                Logger.getLogger("").removeHandler(handler);
            }
        }
        Handler newHandler = getNewHandler();
        Logger.getLogger("").addHandler(newHandler);

        // Update the logging level based on the config.
        changeDebugLevel(config.getNativeHookDebugLevel());
    }

    /************************************************IPC**********************************************************/

    public static synchronized void scheduleExit(long delay) {
        actionExecutor.haltAllTasks();

        GlobalListenerHookController.cleanup();

        new Timer("Delayed exit Timer").schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Writing config file...");
                if (!writeConfigFile()) {
                    System.err.println("Error saving configuration file.");
                    return;
                }
                System.out.println("Wrote config file.");

                if (trayIcon != null) {
                    trayIcon.remove();
                }

                try {
                    IPCServiceManager.stopServices();
                } catch (IOException e) {
                    System.err.println("Unable to stop ipcs\n" + e);
                }
                System.out.println("Goodbye");
                System.exit(0);
            }
        }, delay);
    }


    /*************************************************************************************************************/

    /****************************************Main hotkeys*********************************************************/
    static void configureMainHotkeys() {
        reconfigureSwitchRecord();
        reconfigureSwitchReplay();
        reconfigureSwitchCompiledReplay();
    }

    /*************************************************************************************************************/

    public static void reconfigureSwitchRecord() {
        keysManager.reRegisterTask(switchRecord, ActionInvoker.newBuilder().withHotKey(config.getRECORD()).build());
    }

    public static void reconfigureSwitchReplay() {
        keysManager.reRegisterTask(switchReplay, ActionInvoker.newBuilder().withHotKey(config.getREPLAY()).build());
    }

    public static void reconfigureSwitchCompiledReplay() {
        keysManager.reRegisterTask(switchReplayCompiled, ActionInvoker.newBuilder().withHotKey(config.getCOMPILED_REPLAY()).build());
    }

    /****************************************Record and replay****************************************************/
    public static synchronized void startRecording() {
        if (isRecording) {
            return;
        }
        switchRecord();
    }

    /*************************************************************************************************************/

    public static synchronized void stopRecording() {
        if (!isRecording) {
            return;
        }
        switchRecord();
    }

    private static synchronized void switchRecord() {
        if (isReplaying) { // Do not switch record when replaying.
            return;
        }

        if (!isRecording) { // Start record
            recorder.clear();
            recorder.record();
            isRecording = true;
        } else { // Stop record
            recorder.stopRecord();
            isRecording = false;
        }
    }

    public static void setReplayCount(long count) {
        replayConfig = ReplayConfig.of(count, replayConfig.getDelay(), replayConfig.getSpeedup());
    }

    public static void setReplayDelay(long delay) {
        replayConfig = ReplayConfig.of(replayConfig.getCount(), delay, replayConfig.getSpeedup());
    }

    public static void setReplaySpeedup(float speedup) {
        replayConfig = ReplayConfig.of(replayConfig.getCount(), replayConfig.getDelay(), speedup);
    }

    public static synchronized void startReplay() {
        if (isReplaying) {
            return;
        }
        switchReplay();
    }

    public static synchronized void stopReplay() {
        if (!isReplaying) {
            return;
        }
        switchReplay();
    }

    private static void switchReplay() {
        if (isRecording) { // Cannot switch replay when recording.
            return;
        }

        if (isReplaying) {
            isReplaying = false;
            recorder.stopReplay();
        } else {
            if (!applySpeedup()) {
                return;
            }

            isReplaying = true;
            recorder.replay(replayConfig.getCount(), replayConfig.getDelay(), new Function<>() {
                @Override
                public Void apply(Void r) {
                    switchReplay();
                    return null;
                }
            }, 5, false);
        }
    }

    public static synchronized void runCompiledAction() {
        if (isRunningCompiledTask) {
            return;
        }
        switchRunningCompiledAction();
    }

    public static synchronized void stopRunningCompiledAction() {
        if (!isRunningCompiledTask) {
            return;
        }
        switchRunningCompiledAction();
    }

    private static synchronized void switchRunningCompiledAction() {
        if (isRunningCompiledTask) {
            isRunningCompiledTask = false;
            if (compiledExecutor != null) {
                if (compiledExecutor != Thread.currentThread()) {
                    while (compiledExecutor.isAlive()) {
                        compiledExecutor.interrupt();
                    }
                }
            }
        } else {
            if (customFunction == null) {
                LOGGER.warning("No compiled action in memory");
                return;
            }

            isRunningCompiledTask = true;

            compiledExecutor = new Thread(() -> {
                try {
                    customFunction.action(Core.local(config));
                } catch (InterruptedException e) { // Stopped prematurely
                    return;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Exception caught while executing custom function", e);
                }

                switchRunningCompiledAction();
            });
            compiledExecutor.start();
        }
    }

    /*****************************************Task group related**************************************************/
    static void renderTaskGroup() {
        TaskGroupManager.ensureHasAGroup();
        for (TaskGroup group : taskGroups) {
            if (!group.isEnabled()) {
                continue;
            }

            for (UserDefinedAction task : group.getTasks()) {
                Set<UserDefinedAction> collisions = keysManager.isTaskRegistered(task);
                if (task.isEnabled() && (collisions.isEmpty())) {
                    keysManager.registerTask(task);
                }
            }
        }
    }

    /*************************************************************************************************************/

    public static void addTaskGroup(String name) {
        for (TaskGroup group : taskGroups) {
            if (group.getName().equals(name)) {
                LOGGER.warning("This name already exists. Try again.");
                return;
            }
        }
        taskGroups.add(new TaskGroup(name));
    }

    /*************************************************************************************************************/

    private static void recompiledNativeTasks(Language language) {
        for (TaskGroup group : taskGroups) {
            List<UserDefinedAction> tasks = group.getTasks();
            for (int i = 0; i < tasks.size(); i++) {
                UserDefinedAction task = tasks.get(i);
                if (task.getCompiler() != language) {
                    continue;
                }

                AbstractNativeCompiler compiler = COMPILER_FACTORY.getNativeCompiler(task.getCompiler());
                UserDefinedAction recompiled = task.recompileNative(compiler);
                if (recompiled == null) {
                    continue;
                }

                tasks.set(i, recompiled);

                if (recompiled.isEnabled()) {
                    reRegisterTask(task, recompiled);
                }
            }
        }
    }

    private static void reRegisterTask(UserDefinedAction original, UserDefinedAction action) {
        Set<UserDefinedAction> collisions = keysManager.isTaskRegistered(action);
        boolean conflict = !collisions.isEmpty() && (collisions.size() != 1 || !collisions.iterator().next().equals(original));

        if (!conflict) {
            keysManager.registerTask(action);
        } else {
            List<String> collisionNames = collisions.stream().map(UserDefinedAction::getName).toList();
            LOGGER.warning("Unable to register task " + action.getName() + ". Collisions are " + collisionNames);
        }
    }

    /**
     * Load the source code from the temporary source code file into the text area (if the source code file exists).
     */

    private static void unregisterTask(UserDefinedAction task) {
        keysManager.unregisterTask(task);
    }

    public static void addCurrentTask() {
        addCurrentTask(TaskGroupManager.getCurrentTaskGroup());
    }

    private static void addCurrentTask(TaskGroup group) {
        if (customFunction != null) {
            addTask(customFunction, group);
        } else {
            LOGGER.info("Nothing to add. Compile first?");
        }
    }

    private static void addTask(UserDefinedAction task, TaskGroup group) {
        if (task.getName() == null || task.getName().isBlank()) {
            task.setName("New task");
        }
        group.getTasks().add(task);

        writeConfigFile();
        cleanUnusedSource();
    }

    /**
     * Add task to a special remote task group.
     * If this group does not exist yet, create it.
     */
    public static void addRemoteCompiledTask(UserDefinedAction task) {
        for (TaskGroup group : taskGroups) {
            if (group.getGroupId().equals(TaskGroup.REMOTE_TASK_GROUP_ID)) {
                addTask(task, group);
                return;
            }
        }

        TaskGroup remoteGroup = TaskGroup.remoteTaskGroup();
        taskGroups.add(remoteGroup);
        addTask(task, remoteGroup);
    }

    public static UserDefinedAction getTask(String id) {
        for (TaskGroup group : taskGroups) {
            UserDefinedAction task = group.getTask(id);
            if (task != null) {
                return task;
            }
        }
        LOGGER.severe("Could not get task with ID: " + id);
        throw new NullPointerException("Could not get task with ID: " + id);
    }

    public static void removeCurrentTask(String id) {
        boolean found = false;
        for (ListIterator<UserDefinedAction> iterator = TaskGroupManager.getCurrentTaskGroup().getTasks().listIterator(); iterator.hasNext(); ) {
            UserDefinedAction action = iterator.next();
            if (!action.getActionId().equals(id)) {
                continue;
            }
            found = true;
            unregisterTask(action);
            iterator.remove();
            break;
        }
        if (!found) {
            LOGGER.info("Select a row from the table to remove.");
            return;
        }

        writeConfigFile();
    }

    public static void removeTask(String id) {
        UserDefinedAction toRemove = getTask(id);
//        if (toRemove == null) {
//            return;
//        }
        removeTask(toRemove);
    }

    private static void removeTask(UserDefinedAction toRemove) {
        for (TaskGroup group : taskGroups) {
            for (Iterator<UserDefinedAction> iterator = group.getTasks().iterator(); iterator.hasNext(); ) {
                UserDefinedAction action = iterator.next();
                if (action != toRemove) {
                    continue;
                }
                unregisterTask(action);

                iterator.remove();

                writeConfigFile();
                return;
            }
        }
    }

    public static void moveTaskUp(String taskId) {
        int selected = getTaskIndex(taskId, TaskGroupManager.getCurrentTaskGroup());
        if (selected < 1) {
            return;
        }
        Collections.swap(TaskGroupManager.getCurrentTaskGroup().getTasks(), selected, selected - 1);
    }

    public static void moveTaskDown(String taskId) {
        int selected = getTaskIndex(taskId, TaskGroupManager.getCurrentTaskGroup());
        if (selected >= 0 && selected < TaskGroupManager.getCurrentTaskGroup().getTasks().size() - 1) {
            Collections.swap(TaskGroupManager.getCurrentTaskGroup().getTasks(), selected, selected + 1);
        }
    }

    private static int getTaskIndex(String taskId, TaskGroup group) {
        for (ListIterator<UserDefinedAction> iterator = group.getTasks().listIterator(); iterator.hasNext(); ) {
            int index = iterator.nextIndex();
            UserDefinedAction action = iterator.next();
            if (action.getActionId().equals(taskId)) {
                return index;
            }
        }
        return -1;
    }

    public static void removeTaskGroup(String id) {
        int index = TaskGroupManager.getTaskGroupIndex(id);

        if (index < 0 || index >= taskGroups.size()) {
            return;
        }

        TaskGroup removed = taskGroups.remove(index);
        if (taskGroups.isEmpty()) {
            taskGroups.add(new TaskGroup("default"));
        }

        if (TaskGroupManager.getCurrentTaskGroup() == removed) {
            setCurrentTaskGroup(taskGroups.getFirst());
        }

        for (UserDefinedAction action : removed.getTasks()) {
            keysManager.unregisterTask(action);
        }
        renderTaskGroup();
    }

    public static void changeTaskGroup(String taskId, String newGroupId) {
        int newGroupIndex = TaskGroupManager.getTaskGroupIndex(newGroupId);
        if (newGroupIndex == -1) {
            LOGGER.warning("Cannot change task group to group with ID " + newGroupId + " since it does not exist.");
            return;
        }

        TaskGroup destination = taskGroups.get(newGroupIndex);
        if (destination == TaskGroupManager.getCurrentTaskGroup()) {
            LOGGER.warning("Cannot move to the same group.");
            return;
        }

        if (TaskGroupManager.getCurrentTaskGroup().isEnabled() ^ destination.isEnabled()) {
            LOGGER.warning("Two groups must be both enabled or disabled to move...");
            return;
        }

        UserDefinedAction toMove = null;
        for (Iterator<UserDefinedAction> iterator = TaskGroupManager.getCurrentTaskGroup().getTasks().iterator(); iterator.hasNext(); ) {
            toMove = iterator.next();
            if (toMove.getActionId().equals(taskId)) {
                iterator.remove();
                break;
            }
        }
        if (toMove == null) {
            return;
        }

        destination.getTasks().add(toMove);
        writeConfigFile();
    }

    public static void overwriteTask(String taskId) {
        if (customFunction == null) {
            LOGGER.info("Nothing to override. Compile first?");
            return;
        }

        for (ListIterator<UserDefinedAction> iterator = TaskGroupManager.getCurrentTaskGroup().getTasks().listIterator(); iterator.hasNext(); ) {
            UserDefinedAction action = iterator.next();
            if (!action.getActionId().equals(taskId)) {
                continue;
            }
            customFunction.override(action);

            unregisterTask(action);
            keysManager.registerTask(customFunction);
            iterator.set(customFunction);

            LOGGER.info("Successfully overridden task " + customFunction.getName());
            customFunction = null;
            if (!writeConfigFile()) {
                LOGGER.warning("Unable to update config.");
            }
            break;
        }
        cleanUnusedSource();
    }

    public static void switchEnableTask(UserDefinedAction action) {
        if (action.isEnabled()) { // Then disable it
            action.setEnabled(false);
            if (!action.isEnabled()) {
                keysManager.unregisterTask(action);
            }
        } else { // Then enable it
            Set<UserDefinedAction> collisions = keysManager.isTaskRegistered(action);
            if (!collisions.isEmpty()) {
                GlobalEventsManager.showCollisionWarning(collisions);
                return;
            }

            action.setEnabled(true);
            if (!action.isEnabled()) {
                return;
            }

            // Check if the group containing the action is enabled.
            for (TaskGroup group : taskGroups) {
                if (group.getTask(action.getActionId()) == null) {
                    continue;
                }
                // Found the group. Check if the group is enabled.
                if (!group.isEnabled()) {
                    LOGGER.info("Task " + action.getName() + " is enabled but not registered because the group containing it is not enabled.");
                    return;
                }
            }

            keysManager.registerTask(action);
        }
    }

    /**
     * Populate all tasks with task invoker to dynamically execute other tasks.
     */
//    private static void setTaskInvoker() {
//        for (TaskGroup taskGroup : taskGroups) {
//            for (UserDefinedAction task : taskGroup.getTasks()) {
//                task.setTaskInvoker(taskInvoker);
//            }
//        }
//    }
    public static void importTasks(File inputFile) throws IOException {
        if (OSIdentifier.isWindows()) {
            LOGGER.warning("This feature does not work on Windows (yet), Sorry!");
            return;
        }
        String path = inputFile.getAbsolutePath();
        LOGGER.fine(path);
        LOGGER.finer("Your java runtime is at: " + System.getProperty("java.home"));
        try {
            LOGGER.fine("Exit code of unzip: " + new ProcessBuilder("unzip", "-o", path).inheritIO().start().waitFor());
        } catch (Exception ignored) {
        }
        LOGGER.info("Extracted tasks to import");

        LOGGER.fine("Moving files...");
        try {
//            if (OSIdentifier.isWindows()) {
//                LOGGER.warning("You appear to be using Windows; why?");
//                new ProcessBuilder("XCOPY", "/E", "tmp/data", "data").inheritIO().start().waitFor();
//            } else {
            new ProcessBuilder("cp", "-r", "tmp/data", "data").inheritIO().start().waitFor();
//            }
            LOGGER.fine("Successfully moved files");

            int existingGroupCount = taskGroups.size();
            config.importTaskConfig();
            if (taskGroups.size() > existingGroupCount) {
                LOGGER.info("Successfully imported tasks. Switching to a new task group...");
                TaskGroupManager.setCurrentTaskGroup(taskGroups.get(existingGroupCount)); // Take the new group with lowest index.
//                setTaskInvoker();
            } else {
                LOGGER.warning("No new task group found.");
            }
        } catch (Exception e) {
            LOGGER.warning("Could not import task group!\n" + e);
        }
        LOGGER.fine("Removing useless tmp directory...");
        new ProcessBuilder("rm", "-rf", "tmp").start();
    }

    /**
     * Creates a ConsoleHandler to print LOGGER messages to stderr
     *
     * @return The new ConsoleHandler
     */
    private static ConsoleHandler getNewHandler() {
        ConsoleHandler newHandler = new ConsoleHandler();
        newHandler.setFormatter(new SimpleFormatter() {
            private static final String FORMAT = "[%s] %s %s: %s\n";

            @Override
            public synchronized String format(LogRecord lr) {
                Calendar cal = DateUtility.calendarFromMillis(lr.getMillis());
                String base = String.format(FORMAT, DateUtility.calendarToTimeString(cal), lr.getLoggerName(), lr.getLevel().getLocalizedName(), lr.getMessage());
                StringBuilder builder = new StringBuilder(base);
                if (lr.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    lr.getThrown().printStackTrace(pw);
                    builder.append(sw);
                }
                return builder.toString();
            }
        });
        return newHandler;
    }

    private static void zipDir(File in, String out) {
        if (in == null || out == null) throw new IllegalArgumentException("File(s) may not be null.");
        if (!in.exists()) throw new IllegalArgumentException("Input directory does not not exist.");
        if (!in.isDirectory()) throw new IllegalArgumentException("Not a directory.");
        new FileZipper(in, out);
    }

    public static void exportTasks(File outputDirectory) {
        LOGGER.info(outputDirectory.getAbsolutePath());
        File destination = new File(FileUtility.joinPath(outputDirectory.getAbsolutePath(), "tmp"));
        FileUtility.createDirectory(destination.getAbsolutePath());
        config.exportTasksConfig(destination);
        // Now create a zip file containing all source codes together with the config file
        for (TaskGroup group : taskGroups) {
            for (UserDefinedAction task : group.getTasks()) {
                File sourceFile = new File(task.getSourcePath());
                String destPath = FileUtility.joinPath(destination.getAbsolutePath(), FileUtility.getRelativePwdPath(sourceFile));
                File destFile = new File(destPath);
                FileUtility.copyFile(sourceFile, destFile);
            }
        }
        String zipPath = FileUtility.joinPath(outputDirectory.getAbsolutePath(), "repeat_export.zip");
        zipDir(destination, zipPath);
        LOGGER.info("Data exported to " + zipPath);
    }

    public static void cleanUnusedSource() {
        List<File> files = FileUtility.walk(FileUtility.joinPath("data", "source"));
        Set<String> allNames = new HashSet<>(new Function<File, String>() {
            @Override
            public String apply(File file) {
                return file.getAbsolutePath();
            }
        }.map(files));

        Set<String> using = new HashSet<>();
        for (TaskGroup group : taskGroups) {
            for (UserDefinedAction task : group.getTasks()) {
                List<String> sources = new ArrayList<>();
                String currentSource = new File(task.getSourcePath()).getAbsolutePath();
                sources.add(currentSource);
                sources.addAll(task.getTaskSourceHistory().getEntries().stream().map(e -> new File(e.getSourcePath()).getAbsolutePath()).toList());
                using.addAll(sources);
            }
        }

        allNames.removeAll(using);
        if (allNames.isEmpty()) {
            LOGGER.fine("Nothing to clean");
            return;
        }

        int count = 0, failed = 0;
        for (String name : allNames) {
            if (FileUtility.removeFile(new File(name))) {
                count++;
            } else {
                failed++;
            }
        }
        LOGGER.fine("Successfully cleaned " + count + " files.\n" + failed + " files could not be cleaned");
    }

    public static void setCompilingLanguage(Language language) {
        compilingLanguage = language;
        customFunction = null;
    }

    /***************************************Configurations********************************************************/
    // Write configuration file
    public static boolean writeConfigFile() {
        boolean result = config.save();
        if (!result) {
            LOGGER.warning("Unable to save config.");
        }
        return result;
    }

    /*************************************************************************************************************/

    public static void changeDebugLevel(Level level) {
        LOGGER.fine("Debug level changed to: " + level);
        config.setNativeHookDebugLevel(level);
        Logger.getLogger("").setLevel(level);
        for (Handler h : Logger.getLogger("").getHandlers()) {
            h.setLevel(level);
        }
    }

    public static void haltAllTasks() {
        actionExecutor.haltAllTasks();
    }

    /**
     * Apply the current speedup in the textbox.
     * This attempts to parse the speedup.
     *
     * @return if the speedup was successfully parsed and applied.
     */
    private static boolean applySpeedup() {
        recorder.setSpeedup(replayConfig.getSpeedup());
        return true;
    }

    /*************************************************************************************************************/

    public static void clearLogs() {
        logHolder.clear();
    }

    /*****************************************Task related********************************************************/
    public static RunActionConfig getRunActionConfig() {
        return runActionConfig;
    }

    public static void setRunActionConfig(RunActionConfig config) {
        runActionConfig = config;
    }

    public static boolean changeHotkeyTask(UserDefinedAction action, ActionInvoker newActivation) {
        if (newActivation == null) throw new IllegalArgumentException("Can't add null activation.");

        Set<UserDefinedAction> collisions = keysManager.isActivationRegistered(newActivation);
        collisions.remove(action);
        if (!collisions.isEmpty()) {
            GlobalEventsManager.showCollisionWarning(collisions);
            return false;
        }

        keysManager.reRegisterTask(action, newActivation);
        return true;
    }

    public static String getSourceForTask(UserDefinedAction action, long timestamp) {
        TaskSourceHistoryEntry entry = action.getTaskSourceHistory().findEntry(timestamp);
        if (entry == null) {
            LOGGER.warning("No source path for action " + action.getName() + " at time " + timestamp + ".");
            return null;
        }

        StringBuffer content = FileUtility.readFromFile(entry.getSourcePath());
        if (content == null) {
            LOGGER.warning("No source content for action " + action.getName() + " at file " + entry.getSourcePath() + ".");
            return null;
        }
        return content.toString();
    }

    /********************************************Source code related**********************************************/

    public static String generateSource() {
        String source = "";
        if (applySpeedup()) {
            source = recorder.getGeneratedCode(compilingLanguage);
        }
        return source;
    }

    /*************************************************************************************************************/

    /***************************************Source compilation****************************************************/

    public static Language getSelectedLanguage() {
        return compilingLanguage;
    }

    public static AbstractNativeCompiler getCompiler() {
        return TaskGroupManager.COMPILER_FACTORY.getNativeCompiler(compilingLanguage);
    }

    /**
     * Compile the given source code and sets it as the currently compiled action.
     *
     * @param source   source code to compile.
     * @param taskName name of the newly created task. A default name will be given if not provided.
     */
    public static boolean compileSourceAndSetCurrent(String source, String taskName) {
        AbstractNativeCompiler compiler = getCompiler();
        UserDefinedAction createdInstance = compileSourceNatively(compiler, source, taskName);
        if (createdInstance == null) {
            return false;
        }
        customFunction = createdInstance;
        return true;
    }

    /*************************************************************************************************************/

    public static UserDefinedAction compileSourceNatively(AbstractNativeCompiler compiler, String source, String taskName) {
        source = source.replaceAll("\t", "    "); // Use spaces instead of tabs

        DynamicCompilationResult compilationResult = compiler.compile(source);
        DynamicCompilerOutput compilerStatus = compilationResult.output();
        UserDefinedAction createdInstance = compilationResult.action();
        if (taskName != null && !taskName.isBlank()) {
            createdInstance.setName(taskName);
        }

        if (compilerStatus != DynamicCompilerOutput.COMPILATION_SUCCESS) {
            return null;
        }

//        createdInstance.setTaskInvoker(taskInvoker);
        createdInstance.setCompiler(compiler.getName());

        if (!TaskSourceManager.submitTask(createdInstance, source)) {
            LOGGER.warning("Error writing source file.");
            return null;
        }
        return createdInstance;
    }

    /***************************************Generic Getters and Setters*******************************************/
    public static Core getCore() {
        return Core.local(config);
    }

    public static synchronized boolean isRecording() {
        return isRecording;
    }

    public static synchronized boolean isReplaying() {
        return isReplaying;
    }

    public static synchronized boolean isRunningCompiledAction() {
        return isRunningCompiledTask;
    }

    public static String getLogs() {
        return logHolder.getContent();
    }
}