package core.webui.server.handlers.renderedobjects;

import utilities.StringUtilities;

public class TooltipsIndexPage {
    private static final String mousePosition = StringUtilities.escapeHtml("If enabled, mouse position will be logged on every left control click (key down time).");
    private static final String activeWindowInfosLogging = StringUtilities.escapeHtml("If enabled, active window will be logged on every mouse click (key up time).");
    private static final String record = "Record mouse and keyboard activities.";
    private static final String replay = "Replay recorded activities.";
    private static final String compile = "Compile source code.";
    private static final String run = "Run compiled source code.";
    private static final String editCode = "Edit source code in default editor.";
    private static final String reload = "Reload edited source code after edited in default editor.";
    private static final String add = "Add the compiled action as a new task.";
    private static final String overwrite = "Overwrite selected task with the compiled action.";
    private static final String delete = "Delete selected task.";
    private static final String up = "Move selected task up.";
    private static final String down = "Move selected task down.";
    private static final String changeGroup = "Change the select task's group.";
    private static final String showActionId = "Show task ID.";

    public String getMousePosition() {
        return mousePosition;
    }

    public String getActiveWindowInfosLogging() {
        return activeWindowInfosLogging;
    }

    public String getRecord() {
        return record;
    }

    public String getReplay() {
        return replay;
    }

    public String getCompile() {
        return compile;
    }

    public String getRun() {
        return run;
    }

    public String getEditCode() {
        return editCode;
    }

    public String getReload() {
        return reload;
    }

    public String getRunSelected() {
        return "Run selected task.";
    }

    public String getAdd() {
        return add;
    }

    public String getOverwrite() {
        return overwrite;
    }

    public String getDelete() {
        return delete;
    }

    public String getUp() {
        return up;
    }

    public String getDown() {
        return down;
    }

    public String getChangeGroup() {
        return changeGroup;
    }

    public String getShowActionId() {
        return showActionId;
    }
}