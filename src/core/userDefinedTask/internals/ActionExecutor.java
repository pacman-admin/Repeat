package core.userDefinedTask.internals;

import core.controller.CoreProvider;
import core.userDefinedTask.UserDefinedAction;
import utilities.RandomUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActionExecutor {

    private static final Logger LOGGER = Logger.getLogger(ActionExecutor.class.getName());
    private static final int MAX_SIMULTANEOUS_EXECUTIONS = 1;

    private final HashMap<String, Thread> executions;
    private final CoreProvider coreProvider;
    //private static final ExecutorService actionHandler = newSingleThreadExecutor();

    public ActionExecutor(CoreProvider coreProvider) {
        this.coreProvider = coreProvider;
        this.executions = new HashMap<>(1);
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


    private String startExecutingAction(UserDefinedAction action) {
        return startExecutingAction(ActionExecutionRequest.of(), action);
    }

    /**
     * Start executing an action in a separate thread
     *
     * @param request request for execution of this action
     * @param action  action to execute
     * @return ID of the registered execution
     */
    public String startExecutingAction(ActionExecutionRequest request, UserDefinedAction action) {
        if (executions.size() > MAX_SIMULTANEOUS_EXECUTIONS) {
            //LOGGER.info("Cannot run more than " + MAX_SIMULTANEOUS_EXECUTIONS + " tasks simultaneously.");
            return null;
        }
        if (action == null) {
            throw new IllegalArgumentException("Nothing to run!");
        }
        final String id = RandomUtil.randomID();
        Thread execution = new Thread(() -> {
            try {
                for (int i = 0; i < request.getRepeatCount(); i++) {
                    action.trackedAction(coreProvider.get());
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
        return id;
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