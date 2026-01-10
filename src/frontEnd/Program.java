package frontEnd;

import core.config.Config;
import core.controller.Core;
import core.ipc.repeatServer.processors.TaskProcessorManager;
import core.keyChain.managers.GlobalEventsManager;
import core.languageHandler.Language;
import core.recorder.Recorder;
import core.recorder.ReplayConfig;
import core.userDefinedTask.TaskInvoker;
import core.userDefinedTask.UserDefinedAction;
import core.userDefinedTask.internals.ActionExecutor;
import core.userDefinedTask.internals.RunActionConfig;
import staticResources.BootStrapResources;
import utilities.Function;
import utilities.logging.LogHolder;

import java.awt.*;
import java.io.File;
import java.util.logging.Logger;

public final class Program {
    static final Logger LOGGER = Logger.getLogger(Program.class.getName());
    static MinimizedFrame trayIcon;
    static Thread compiledExecutor;
    static UserDefinedAction customFunction;
    static boolean isRecording, isReplaying, isRunningCompiledTask;
    static File currentTempFile;

    static {
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
        try {
            LOGGER.info("Adding tray icon...");
            trayIcon = new MinimizedFrame(BootStrapResources.TRAY_IMAGE);
            LOGGER.info("Tray Icon added");
        } catch (Exception e) {
            LOGGER.warning("Could not add tray icon!\n" + e.getMessage());
        }
        trayIcon.add();
    }

    static LogHolder logHolder = new LogHolder();
    static Language compilingLanguage = Language.MANUAL_BUILD;
    static Config config = Config.loadFromFile();
    static TaskInvoker taskInvoker = new TaskInvoker(Core.local(config));
    static ActionExecutor actionExecutor = new ActionExecutor(Core.local(config));
    static GlobalEventsManager keysManager = new GlobalEventsManager(config, actionExecutor);
    static ReplayConfig replayConfig = ReplayConfig.of();
    static RunActionConfig runActionConfig = RunActionConfig.of();
    static Recorder recorder = new Recorder(Core.local(config));

    static UserDefinedAction switchRecord = new UserDefinedAction() {
        @Override
        public void action(Core controller) {
            Backend.switchRecord();
        }
    };
    static UserDefinedAction switchReplay = new UserDefinedAction() {
        @Override
        public void action(Core controller) {
            Backend.switchReplay();
        }
    };
    static UserDefinedAction switchReplayCompiled = new UserDefinedAction() {
        @Override
        public void action(Core controller) {
            Backend.switchRunningCompiledAction();
        }
    };
}