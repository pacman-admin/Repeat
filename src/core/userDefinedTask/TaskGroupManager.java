package core.userDefinedTask;

import argo.jdom.JsonNode;
import core.config.ParsingMode;
import core.languageHandler.compiler.DynamicCompilerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public final class TaskGroupManager {
    public static final DynamicCompilerManager COMPILER_FACTORY = new DynamicCompilerManager();
    public static final List<TaskGroup> taskGroups = new ArrayList<>();
    private static TaskGroup currentGroup = new TaskGroup("default");

    public static void parseJSON(List<JsonNode> taskGroupData) {
        parseJSON(taskGroupData, ParsingMode.DEFAULT);
        if(taskGroups.isEmpty()){
            taskGroups.addFirst(currentGroup);
        }
        currentGroup = taskGroups.getFirst();
    }

    public static void parseJSON(List<JsonNode> taskGroupData, ParsingMode mode) {
        for (JsonNode node : taskGroupData) {
            TaskGroup taskGroup = TaskGroup.parseJSON(COMPILER_FACTORY, node, mode);
            if (taskGroup != null) {
                taskGroups.add(taskGroup);
            }
        }
    }

    /**
     * Get the task group with the given id, or null if no such group exists.
     */
    public static TaskGroup getTaskGroup(String id) {
        for (TaskGroup group : taskGroups) {
            if (group.getGroupId().equals(id)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Retrieve an immutable view of the list of task groups.
     */
    public static List<TaskGroup> getTaskGroups() {
        return Collections.unmodifiableList(taskGroups);
    }

    public static TaskGroup getCurrentTaskGroup() {
        return currentGroup;
    }

    public static void setCurrentTaskGroup(TaskGroup currentTaskGroup) {
        if (taskGroups.contains(currentTaskGroup)) {
            currentGroup = currentTaskGroup;
        }
    }

    public static int getTaskGroupIndex(String id) {
        for (ListIterator<TaskGroup> iterator = taskGroups.listIterator(); iterator.hasNext(); ) {
            int index = iterator.nextIndex();
            TaskGroup group = iterator.next();
            if (group.getGroupId().equals(id)) {
                return index;
            }
        }
        return -1;
    }
    /**
     * Populate all tasks with task invoker to dynamically execute other tasks.
     */

    public static void moveTaskGroupUp(String id) {
        int index = getTaskGroupIndex(id);
        if (index < 1) {
            return;
        }
        Collections.swap(taskGroups, index, index - 1);
    }

    public static void moveTaskGroupDown(String id) {
        int index = getTaskGroupIndex(id);

        if (index >= 0 && index < taskGroups.size() - 1) {
            Collections.swap(taskGroups, index, index + 1);
        }
    }
}