package core.userDefinedTask;

import core.controller.Core;
import core.keyChain.ActionInvoker;

import java.util.logging.Logger;

import static core.userDefinedTask.TaskGroupManager.taskGroups;

/**
 * Class to invoke tasks programmatically.
 */
public final class TaskInvoker {

    private static final Logger LOGGER = Logger.getLogger(TaskInvoker.class.getName());

    private final Core core;

    public TaskInvoker(final Core controller) {
        this.core = controller;
        LOGGER.info("Created TaskInvoker");
    }

    /**
     * Execute a task. Emit a warning and no-op if there is no such action.
     *
     * @param groupIndex the index of the group that the task belongs to.
     * @param taskIndex  the index of the task within the group.
     * @throws InterruptedException
     */
    public void execute(int groupIndex, int taskIndex) throws InterruptedException {
        execute(groupIndex, taskIndex, ActionInvoker.newBuilder().build());
    }

    /**
     * Execute a task. Emit a warning and no-op if there is no such task.
     *
     * @param groupIndex the index of the group that the task belongs to.
     * @param taskIndex  the index of the task within the group.
     * @throws InterruptedException
     */
    private void execute(int groupIndex, int taskIndex, ActionInvoker activation) throws InterruptedException {
        if (groupIndex >= taskGroups.size()) {
            LOGGER.warning(String.format("Unable to execute task in group with index %d. There are only %d group(s).", groupIndex, taskGroups.size()));
            return;
        }
        TaskGroup group = taskGroups.get(groupIndex);

        if (taskIndex >= group.getTasks().size()) {
            LOGGER.warning(String.format("Unable to execute task in with index %d. Group %s only has %d tasks.", taskIndex, group.getName(), group.getTasks().size()));
            return;
        }
        UserDefinedAction task = group.getTasks().get(taskIndex);
        execute(task, activation);
    }

    /**
     * Execute a task. Emit a warning and no-op if there is no such task.
     *
     * @param id ID of the task.
     */
    public void execute(String id) throws InterruptedException {
        LOGGER.info("Executing task: " + id);
        execute(id, ActionInvoker.newBuilder().build());
    }

    /**
     * Execute a task. Emit a warning and no-op if there is no such task.
     *
     * @param id         ID of the task.
     * @param activation task activation to associate with the execution.
     */
    private void execute(String id, ActionInvoker activation) throws InterruptedException {
        for (TaskGroup group : taskGroups) {
            for (UserDefinedAction task : group.getTasks()) {
                if (task.getActionId().equals(id)) {
                    execute(task, activation);
                    LOGGER.info("Executing task: " + task.getName());
                    return;
                }
            }
        }
        LOGGER.warning("Cannot find task with ID " + id + ".");
    }

    private void execute(UserDefinedAction action, ActionInvoker activation) throws InterruptedException {
        LOGGER.info("Executing task: " + action.getName());
        action.setInvoker(activation);
        action.trackedAction(core);
    }
}
