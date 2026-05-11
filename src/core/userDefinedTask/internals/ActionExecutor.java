package core.userDefinedTask.internals;

import core.controller.Core;
import core.userDefinedTask.UserDefinedAction;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ActionExecutor {

    private static final Logger LOGGER = Logger.getLogger(ActionExecutor.class.getName());
    private final Core core;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> execution;

    public ActionExecutor(Core controller) {
        this.core = controller;
        execution = executor.submit(() -> LOGGER.fine("Main executor started"));
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
    @SuppressWarnings("BusyWait")
    public void startExecutingAction(ActionExecutionRequest request, UserDefinedAction action) {
        if (execution.isDone()) {
            try {
                execution = executor.submit(() -> {
                    try {
                        for (int i = 0; i < request.getRepeatCount(); i++) {
                            action.trackedAction(core);
                            Thread.sleep(request.getDelayMsBetweenRepeat());
                        }
                    } catch (InterruptedException e) {
                        LOGGER.fine("Task ended prematurely");
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Exception while executing task " + action.getName(), e);
                    }
                });
            } catch (RejectedExecutionException ignored) {
            } catch (NullPointerException ignored) {
                LOGGER.info("Nothing to run");
            }
        }
    }

    /**
     * Interrupt all currently executing tasks, and clear the record of all executing tasks
     */
    public void haltAllTasks() {
        if (execution == null) return;
        execution.cancel(true);
        LOGGER.fine("Halting current task...");
    }

    public void shutdown() {
        haltAllTasks();
        executor.shutdownNow();
        LOGGER.fine("Shutting down main executor...");
        try {
            if (executor.awaitTermination(15, TimeUnit.SECONDS)) {
                LOGGER.fine("Main executor shut down");
                return;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.fine("Error halting all tasks");
    }
}