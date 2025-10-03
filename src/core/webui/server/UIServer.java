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
package core.webui.server;

import core.config.WebUIConfig;
import core.ipc.IPCServiceWithModifablePort;
import core.keyChain.TaskActivationConstructorManager;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.webui.server.handlers.AboutPageHandler;
import core.webui.server.handlers.ApiPageHandler;
import core.webui.server.handlers.IndexPageHandler;
import core.webui.server.handlers.internals.*;
import core.webui.server.handlers.internals.globalconfigs.GlobalConfigsPageHandler;
import core.webui.server.handlers.internals.globalconfigs.SetCoreConfigClientsHandler;
import core.webui.server.handlers.internals.globalconfigs.SetToolsConfigClientsHandler;
import core.webui.server.handlers.internals.ipcs.*;
import core.webui.server.handlers.internals.logs.*;
import core.webui.server.handlers.internals.menu.*;
import core.webui.server.handlers.internals.recordsreplays.*;
import core.webui.server.handlers.internals.taskactivation.*;
import core.webui.server.handlers.internals.taskcreation.*;
import core.webui.server.handlers.internals.taskgroups.*;
import core.webui.server.handlers.internals.taskmanagement.*;
import core.webui.server.handlers.internals.tasks.*;
import core.webui.server.handlers.internals.tasks.manuallybuild.*;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpHandlerWithBackend;
import core.webui.webcommon.StaticFileServingHandler;
import core.webui.webcommon.UpAndRunningHandler;
import frontEnd.MainBackEndHolder;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("FieldMayBeFinal")
public class UIServer extends IPCServiceWithModifablePort {

    private static final int TERMINATION_DELAY_SECOND = 2;
    private final ObjectRenderer objectRenderer;
    private final TaskSourceCodeFragmentHandler taskSourceCodeFragmentHandler;
    private Map<String, HttpHandlerWithBackend> handlers;
    private MainBackEndHolder backEndHolder;
    private TaskActivationConstructorManager taskActivationConstructorManager;
    private ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;
    private Thread mainThread;
    private HttpServer server;

    public UIServer() {
        setPort(WebUIConfig.DEFAULT_SERVER_PORT);

        taskActivationConstructorManager = new TaskActivationConstructorManager();
        manuallyBuildActionConstructorManager = ManuallyBuildActionConstructorManager.of();
        objectRenderer = new ObjectRenderer();
        taskSourceCodeFragmentHandler = new TaskSourceCodeFragmentHandler(objectRenderer, manuallyBuildActionConstructorManager);
    }

    public synchronized void setMainBackEndHolder(MainBackEndHolder backEndHolder) {
        this.backEndHolder = backEndHolder;
        if (handlers == null) {
            return;
        }

        for (HttpHandlerWithBackend handler : handlers.values()) {
            handler.setMainBackEndHolder(backEndHolder);
        }
    }

    private Map<String, HttpHandlerWithBackend> createHandlers() {
        Map<String, HttpHandlerWithBackend> output = new HashMap<>();
        output.put("/", new IndexPageHandler(objectRenderer, manuallyBuildActionConstructorManager));
        //output.put("/logs", new LogsPageHandler(objectRenderer));
        output.put("/ipcs", new IPCPageHandler(objectRenderer));
        //output.put("/global-configs", new GlobalConfigsPageHandler(objectRenderer));
        output.put("/task-groups", new TaskGroupsPageHandler(objectRenderer));
        output.put("/tasks/details", new TaskDetailsPageHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/tasks/manually-build", new TaskBuilderPageHandler(objectRenderer, manuallyBuildActionConstructorManager));
        output.put("/api", new ApiPageHandler());
        output.put("/about", new AboutPageHandler(objectRenderer));

        //output.put("/internals/global-configs/tools-config/set-clients", new SetToolsConfigClientsHandler(objectRenderer));
        //output.put("/internals/global-configs/core-config/set-clients", new SetCoreConfigClientsHandler(objectRenderer));

        output.put("/internals/menu/file/save-config", new MenuSaveConfigActionHandler());
        output.put("/internals/menu/file/import-tasks", new MenuImportTaskActionHandler());
        output.put("/internals/menu/file/export-tasks", new MenuExportTaskActionHandler());
        output.put("/internals/menu/file/clean-unused-sources", new MenuCleanUnusedSourcesActionHandler());
        output.put("/internals/menu/file/force-exit", new MenuForceExitActionHandler());
        output.put("/internals/menu/file/exit", new MenuExitActionHandler());

        output.put("/internals/menu/tools/halt-all-tasks", new MenuHaltAllTasksActionHandler());
        output.put("/internals/menu/tools/generate-source", new MenuGetGeneratedSourceHandler(taskSourceCodeFragmentHandler));
        output.put("/internals/menu/tools/get-compiling-languages-options", new MenuGetCompilingLanguagesActionHandler(objectRenderer));
        output.put("/internals/menu/tools/set-compiling-language", new MenuSetCompilingLanguagesActionHandler(taskSourceCodeFragmentHandler));

        output.put("/internals/menu/settings/get-compiler-path", new MenuGetCompilerPathActionHandler());
        output.put("/internals/menu/settings/set-compiler-path", new MenuSetCompilerPathActionHandler());
        output.put("/internals/menu/settings/compiler-config-options", new MenuGetCompilerConfigOptionActionHandler(objectRenderer));
        output.put("/internals/menu/settings/set-compiler-config", new MenuSetCompilerConfigActionHandler());
        output.put("/internals/menu/settings/record-mouse-click-only", new MenuRecordMouseClickOnlyActionHandler());
        output.put("/internals/menu/settings/halt-task-by-escape", new MenuHaltTaskByEscapeActionHandler());
        output.put("/internals/menu/settings/debug-level-options", new MenuGetDebugLevelOptionsActionHandler(objectRenderer));
        output.put("/internals/menu/settings/set-debug-level", new MenuSetDebugLevelActionHandler());
        output.put("/internals/menu/settings/execute-on-release", new MenuExecuteOnReleaseActionHandler());
        output.put("/internals/menu/settings/use-clipboard-to-type-string", new MenuUseClipboardToTypeStringActionHandler());
        output.put("/internals/menu/settings/run-task-with-server-config", new MenuUseClipboardToTypeStringActionHandler());
        output.put("/internals/menu/settings/use-tray-icon", new MenuUseTrayIconActionHandler());
        output.put("/internals/menu/settings/use-java-awt-for-mouse-position", new MenuUseJavaAwtForMousePosition());

        output.put("/internals/action/task-details/save", new ActionSaveTaskDetailsHandler(objectRenderer, taskActivationConstructorManager));

        output.put("/internals/action/task-activation/start-listening", new ActionTaskActivationStartListeningHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/stop-listening", new ActionTaskActivationStopListeningHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/key-chain/remove", new ActionTaskActivationRemoveKeyChainHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/key-sequence/remove", new ActionTaskActivationRemoveKeySequenceHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/mouse-gestures/set", new ActionTaskActivationSetMouseGesturesHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/phrase/add", new ActionTaskActivationAddPhraseHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/phrase/remove", new ActionTaskActivationRemovePhraseHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/shared-variables/add", new ActionTaskActivationAddSharedVariables(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/shared-variables/remove", new ActionTaskActivationRemoveSharedVariables(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/strokes/add-mouse-key", new ActionTaskActivationAddMouseKey(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/strokes/add-as-key-chain", new ActionTaskActivationAddStrokesAsKeyChainHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/strokes/add-as-key-sequence", new ActionTaskActivationAddStrokesAsKeySequenceHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/strokes/get", new ActionTaskActivationGetStrokesHandler(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/global-key-action/released/set", new ActionTaskActivationSetGlobalKeyAction(objectRenderer, taskActivationConstructorManager));
        output.put("/internals/action/task-activation/global-key-action/pressed/set", new ActionTaskActivationSetGlobalKeyAction(objectRenderer, taskActivationConstructorManager));

        output.put("/internals/action/manually-build/constructor/params-placeholder", new ActionManuallyBuildActionParametersPlaceHolderHandler());
        output.put("/internals/action/manually-build/constructor/possible-actions", new ActionManuallyBuildActionListActionsForActorHandler(objectRenderer));
        output.put("/internals/action/manually-build/constructor/insert-step", new ActionManuallyBuildActionInsertStepHandler(objectRenderer, manuallyBuildActionConstructorManager));
        output.put("/internals/action/manually-build/constructor/remove-steps", new ActionManuallyBuildActionRemoveStepsHandler(objectRenderer, manuallyBuildActionConstructorManager));
        output.put("/internals/action/manually-build/constructor/move-up", new ActionManuallyBuildActionMoveUpHandler(objectRenderer, manuallyBuildActionConstructorManager));
        output.put("/internals/action/manually-build/constructor/move-down", new ActionManuallyBuildActionMoveDownHandler(objectRenderer, manuallyBuildActionConstructorManager));
        output.put("/internals/action/manually-build/constructor/build", new ActionManuallyBuildActionBuilldAction(manuallyBuildActionConstructorManager));

        output.put("/internals/action/add-task", new ActionAddTaskHandler(objectRenderer));
        output.put("/internals/action/add-task-group", new ActionAddTaskGroupHandler(objectRenderer));
        output.put("/internals/action/change-task-group-name", new ActionChangeTaskGroupNameHandler(objectRenderer));
        output.put("/internals/action/change-task-group-for-task", new ActionChangeTaskGroupForTaskHandler(objectRenderer));
        output.put("/internals/action/change-replay-config", new ActionChangeReplayConfigHandler());
        output.put("/internals/action/clear-log", new ActionClearLogHandler());
        output.put("/internals/action/compile-task", new ActionCompileTaskHandler());
        output.put("/internals/action/delete-task", new ActionDeleteTaskHandler(objectRenderer));
        output.put("/internals/action/delete-task-group", new ActionDeleteTaskGroupHandler(objectRenderer));
        output.put("/internals/action/edit-source", new ActionEditSourceHandler());
        output.put("/internals/action/move-task-up", new ActionMoveTaskUpHandler(objectRenderer));
        output.put("/internals/action/move-task-group-up", new ActionMoveTaskGroupUpHandler(objectRenderer));
        output.put("/internals/action/move-task-down", new ActionMoveTaskDownHandler(objectRenderer));
        output.put("/internals/action/move-task-group-down", new ActionMoveTaskGroupDownHandler(objectRenderer));
        output.put("/internals/action/overwrite-task", new ActionOverwriteTaskHandler(objectRenderer));
        output.put("/internals/action/run", new RunTaskHandler());
        output.put("/internals/action/run-config/save", new SaveRunTaskConfigHandler());
        output.put("/internals/action/run-config/get", new GetRunTaskConfigHandler(objectRenderer));
        output.put("/internals/action/run-compiled-task", new ActionRunCompiledTaskHandler());
        //output.put("/internals/action/run-ipc-service", new ActionRunIPCServiceHandler(objectRenderer));
        output.put("/internals/action/start-record", new ActionStartRecordingHandler());
        output.put("/internals/action/start-replay", new ActionStartReplayHandler());
        output.put("/internals/action/stop-record", new ActionStopRecordingHandler());
        output.put("/internals/action/stop-replay", new ActionStopReplayHandler());
        //output.put("/internals/action/stop-ipc-service", new ActionStopIPCServiceHandler(objectRenderer));
        output.put("/internals/action/stop-running-compiled-task", new ActionStopRunningCompiledTaskHandler());
        output.put("/internals/action/switch-task-group", new ActionSwitchTaskGroupHandler(objectRenderer));

        //noinspection SpellCheckingInspection
        output.put("/internals/get/editted-source", new GetEditedSourceHandler());
        output.put("/internals/get/is-running-compiled-task", new GetIsRunningCompiledTaskHandler());
        output.put("/internals/get/is-recording", new GetIsRecordingHandler());
        output.put("/internals/get/is-replaying", new GetIsReplayingHandler());
        output.put("/internals/get/is-mouse-position-logging-enabled", new GetIsMousePositionLoggingEnabledHandler());
        output.put("/internals/get/is-active-window-info-logging-enabled", new GetIsActiveWindowInfosLoggingEnabledHandler());
        output.put("/internals/get/logs", new GetLogsHandler());
        output.put("/internals/get/mouse-position", new GetMousePositionHandler());
        output.put("/internals/get/path-suggestion", new GetPathSuggestionHandler());
        output.put("/internals/get/source-templates", new GetSourceTemplateHandler());
        output.put("/internals/get/task-source", new GetTaskSourceHandler());
        output.put("/internals/get/rendered-task-groups-dropdown", new GetRenderedTaskGroupsDropdown(objectRenderer));
        output.put("/internals/get/rendered-task-groups-select-modal", new GetRenderedTaskGroupsSelectModalHandler(objectRenderer));

        output.put("/internals/set/selected-task", new SetSelectedTaskHandler(taskSourceCodeFragmentHandler));
        output.put("/internals/set/mouse-position-logging-enabled", new SetMousePositionLoggingEnabledHandler());
        output.put("/internals/set/active-window-info-logging-enabled", new SetActiveWindowInfosLoggingEnabledHandler());

        output.put("/internals/modify/ipc-service-port", new ModifyIPCServicePortHandler(objectRenderer));
        output.put("/internals/modify/task-name", new ModifyTaskNameHandler(objectRenderer));

        //output.put("/internals/toggle/ipc-service-launch-at-startup", new ToggleIPCServiceLaunchAtStartupHandler(objectRenderer));
        output.put("/internals/toggle/task-group-enabled", new ToggleTaskGroupEnabledHandler(objectRenderer));
        output.put("/internals/toggle/task-enabled", new ToggleTaskEnabledHandler(objectRenderer));

        return output;
    }

    @Override
    protected void start() throws IOException {
        if (!portFree()) {
            getLogger().warning("Failed to initialize " + getName() + ". Port " + port + " is not free.");
            throw new IOException("Port " + port + " is not free.");
        }

        handlers = createHandlers();
        taskActivationConstructorManager.start();
        manuallyBuildActionConstructorManager.start();
        setMainBackEndHolder(backEndHolder);

        ServerBootstrap serverBootstrap = ServerBootstrap.bootstrap().setLocalAddress(InetAddress.getByName("localhost")).setIOReactorConfig(IOReactorConfig.custom().setSoReuseAddress(true).build()).setListenerPort(port).setServerInfo("Repeat").setExceptionLogger(new UIServerExceptionLogger()).registerHandler("/test", new UpAndRunningHandler()).registerHandler("/static/*", new StaticFileServingHandler());
        for (Entry<String, HttpHandlerWithBackend> entry : handlers.entrySet()) {
            serverBootstrap.registerHandler(entry.getKey(), entry.getValue());
        }
        server = serverBootstrap.create();

        mainThread = new Thread(() -> {
            try {
                server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                getLogger().log(Level.SEVERE, "Interrupted when waiting for UI server.", e);
            }
            getLogger().info("Finished waiting for UI server termination...");
        });
        server.start();
        mainThread.start();
        getLogger().info("UI server started!");
    }

    @Override
    protected void stop() {
        taskActivationConstructorManager.stop();
        manuallyBuildActionConstructorManager.stop();
        server.shutdown(TERMINATION_DELAY_SECOND, TimeUnit.SECONDS);
        try {
            mainThread.join();
            getLogger().info("UI server terminated...");
        } catch (InterruptedException e) {
            getLogger().log(Level.WARNING, "Interrupted when waiting for server to terminate.", e);
        }
        server = null;
        mainThread = null;
        handlers.clear();
    }

    @Override
    public boolean setPort(int newPort) {
        boolean isRunning = isRunning();

        if (isRunning) {
            getLogger().info("Restarting UI server to change port to " + newPort);
            stop();
        }
        this.port = newPort;

        if (!isRunning) {
            return true;
        }
        try {
            start();
            getLogger().info("UI server restarted at port " + newPort);
            return true;
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "IOException when starting UI server after changing port.", e);
            return false;
        }
    }

    @Override
    public boolean isRunning() {
        return mainThread != null && server != null;
    }

    @Override
    public String getName() {
        return "UI server";
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(UIServer.class.getName());
    }

    public ManuallyBuildActionConstructorManager getManuallyBuildActionConstructorManager() {
        return manuallyBuildActionConstructorManager;
    }

    private boolean portFree() throws IOException {
        try {
            ServerSocket socket = new ServerSocket(port);
            socket.close();
            return true;
        } catch (BindException e) {
            getLogger().warning("Bind exception on port " + port + ". ");
        }
        return false;
    }
}
