package core.userDefinedTask.internals;

import core.controller.Core;
import core.userDefinedTask.UserDefinedAction;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ActionExecutor {

    private static final Logger LOGGER = Logger.getLogger(ActionExecutor.class.getName());
    private static final int MAX_SIMULTANEOUS_EXECUTIONS = 2;
    //private final HashSet<Future<?>> executions;
    private final Core core;
    private ExecutorService executor = Executors.newFixedThreadPool(MAX_SIMULTANEOUS_EXECUTIONS);

    public ActionExecutor(Core controller) {
        this.core = controller;
        //this.executions = new HashSet<>();
    }

    /**
     * Start executing actions, each in a separate thread.
     *
     * @param actions actions to execute.
     */
    public void startExecutingActions(Collection<UserDefinedAction> actions) {
        actions.forEach(this::startExecutingAction);
    }


    private void startExecutingAction(UserDefinedAction action) {
        startExecutingAction(ActionExecutionRequest.of(), action);
    }

    /**
     * Start executing an action in a separate thread
     *
     * @param request request for execution of this action
     * @param action  action to execute
     */
    public void startExecutingAction(ActionExecutionRequest request, UserDefinedAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Nothing to run.");
        }
        if (executor.isShutdown()) {
            executor = Executors.newFixedThreadPool(MAX_SIMULTANEOUS_EXECUTIONS);
        }
        executor.submit(() -> {
            try {
                for (int i = 0; i < request.getRepeatCount(); i++) {
                    action.trackedAction(core);
                    Thread.sleep(request.getDelayMsBetweenRepeat());
                }
            } catch (InterruptedException e) {
                LOGGER.info("Task ended prematurely");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Exception while executing task " + action.getName(), e);
            }
        });
    }

    /**
     * Interrupt all currently executing tasks, and clear the record of all executing tasks
     */
    public void haltAllTasks() {
        executor.shutdownNow();
        LOGGER.info("Halting all tasks...");
        try {
            if (executor.awaitTermination(5L, TimeUnit.SECONDS)) {
                LOGGER.info("All tasks halted");
                return;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.warning("Error halting all tasks");
    }
}