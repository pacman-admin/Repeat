package core.webui.server.handlers;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.TaskGroupManager;
import core.userDefinedTask.UserDefinedAction;
import frontEnd.MainBackEndHolder;

import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class CommonTask {

    private static final Logger LOGGER = Logger.getLogger(CommonTask.class.getName());

    private CommonTask() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    public static IIPCService getIPCService(Map<String, String> params) {
        return IPCServiceManager.getUIServer();
    }

    public static UserDefinedAction getTaskFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params) {
        String taskId = getTaskIdFromRequest(params);
        if (taskId.isEmpty()) {
            LOGGER.warning("Cannot find task ID.");
            return null;
        }

        return getTaskFromId(backEndHolder, taskId);
    }

    public static String getTaskIdFromRequest(Map<String, String> params) {
        String taskValue = params.get("task");
        if (taskValue == null || taskValue.isEmpty()) {
            LOGGER.warning("Missing task ID.");
            return "";
        }

        return taskValue;
    }

    public static UserDefinedAction getTaskFromId(MainBackEndHolder backEndHolder, String id) {
        UserDefinedAction task = backEndHolder.getTask(id);
        if (task == null) {
            LOGGER.warning("No such task with ID " + id + ".");
            return null;
        }

        return task;
    }

    public static String getTaskGroupIdFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params) {
        String groupValue = params.get("group");
        if (groupValue == null || groupValue.isEmpty()) {
            LOGGER.warning("Group ID must not be empty.");
            return null;
        }

        return groupValue;
    }

    public static TaskGroup getTaskGroupFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params, boolean useCurrentIfNotProvided) {
        String groupValue = params.get("group");
        if (groupValue == null) {
            if (useCurrentIfNotProvided) {
                return TaskGroupManager.getCurrentTaskGroup();
            }
            return null;
        }

        String id = getTaskGroupIdFromRequest(backEndHolder, params);
        if (id == null) {
            LOGGER.warning("Could not gt TaskGroup from request");
            return null;
        }
        return TaskGroupManager.getTaskGroup(id);
    }
}
