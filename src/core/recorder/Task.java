package core.recorder;


public final class Task {
	private static final Task EARLY_TASK = new Task(Long.MIN_VALUE, null);
	private final long time;
	private final Runnable task;

	private Task(long time, Runnable task) {
		this.time = time;
		this.task = task;
	}
}
