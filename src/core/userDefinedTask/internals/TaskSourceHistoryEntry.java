package core.userDefinedTask.internals;

import utilities.FileUtility;

import java.util.Calendar;

/**
 * Holds a single entry of source history.
 */
public final class TaskSourceHistoryEntry {
    private final String sourcePath;
    private final Calendar created;

    private TaskSourceHistoryEntry(String sourcePath, Calendar created) {
        this.sourcePath = sourcePath;
        this.created = created;
    }

    public static TaskSourceHistoryEntry of(String path) {
        return new TaskSourceHistoryEntry(path, Calendar.getInstance());
    }

    public static TaskSourceHistoryEntry of(String path, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return new TaskSourceHistoryEntry(path, calendar);
    }


    public String getSourcePath() {
        return sourcePath;
    }
    public String getAbsSourcePath(){
        return FileUtility.getPath(sourcePath);
    }

    public Calendar getCreated() {
        return created;
    }
}
