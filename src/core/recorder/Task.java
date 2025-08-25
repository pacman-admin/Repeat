package core.recorder;


public class Task {
	protected static final Task EARLY_TASK = new Task(Long.MIN_VALUE, null);

    private Task(long time, Runnable task) {
    }
}
