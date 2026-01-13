package core.userDefinedTask.internals;

import core.controller.Core;
import core.userDefinedTask.UserDefinedAction;
import utilities.RandomUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ActionExecutor {

    private static final Logger LOGGER = Logger.getLogger(ActionExecutor.class.getName());
    private static final int MAX_SIMULTANEOUS_EXECUTIONS = 3;

    private final HashMap<String, Thread> executions;
    private final Core core;

    public ActionExecutor(Core controller) {
        this.core = controller;
        this.executions = HashMap.newHashMap(MAX_SIMULTANEOUS_EXECUTIONS + 1);
    }

    /**
     * Start executing actions, each in a separate thread.
     *
     * @param actions actions to execute.
     */
    public void startExecutingActions(Collection<UserDefinedAction> actions) {
        for (UserDefinedAction action : actions) {
            startExecutingAction(action);
        }
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
        if (executions.size() >= MAX_SIMULTANEOUS_EXECUTIONS) {
            //LOGGER.info("Cannot run more than " + MAX_SIMULTANEOUS_EXECUTIONS + " tasks simultaneously.");
            return;
        }
        if (action == null) {
            throw new IllegalArgumentException("Nothing to run.");
        }
        final String id = RandomUtil.randomID();
        Thread execution = new Thread(() -> {
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
            executions.remove(id);
        }, "Execution thread for Action " + action.getName() + "ID: " + action.getActionId());

        executions.put(id, execution);
        execution.start();
    }

    /**
     * Interrupt all currently executing tasks, and clear the record of all executing tasks
     */
    public void haltAllTasks() {
        LinkedList<Thread> endingThreads = new LinkedList<>(executions.values());
        for (Thread thread : endingThreads) {
            LOGGER.info("Halting execution thread " + thread.getName());
            while (thread.isAlive() && thread != Thread.currentThread()) {
                thread.interrupt();
            }
        }
        executions.clear();
    }
}