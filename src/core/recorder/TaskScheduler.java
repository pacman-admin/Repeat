package core.recorder;

import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Function;
import core.scheduler.AbstractScheduler;
import core.scheduler.SchedulingData;

class TaskScheduler extends AbstractScheduler<Runnable> {

	private static final Logger LOGGER = Logger.getLogger(TaskScheduler.class.getName());

	static {
		LOGGER.setLevel(Level.ALL);
	}

	private Thread executeAgent;
	private boolean isRunning;

	TaskScheduler() {
		super();
	}

	synchronized long runTasks(final long count, final long delay, final float speedup,
                               final Function<Void, Void> callBack, final long callBackDelay) {
		if (isRunning) {
			LOGGER.info("Cannot invoke two running instances");
			return 0;
		} else if (count < 1) {
			LOGGER.warning("Attempt to run tasks with count " + count);
			return 0;
		} else if (delay < 0) {
			LOGGER.warning("Attempt to run tasks with negative delay " + delay);
			return 0;
		} else if (callBackDelay < 0) {
			LOGGER.warning("Attempt to run tasks with negative callBack delay " + callBackDelay);
			return 0;
		}

		isRunning = true;
		Runnable running = () -> {
            for (long i = 0; i < count; i++) {
                long time = 0;
                for (SchedulingData<Runnable> t : tasks) {
                    long currentTime = t.getTime();

                    if (currentTime < time) {
                        LOGGER.severe("Something went really bad");
                        System.exit(1);
                    }

                    try {
                        Thread.sleep((long)((currentTime - time) / speedup));
                    } catch (InterruptedException e) {
                        LOGGER.info("Ended prematurely");
                        return; // Ended prematurely
                    }

                    time = currentTime;
                    t.getData().run();
                }

                if (delay > 0) {
                    try {
                        Thread.sleep((long)(delay / speedup));
                    } catch (InterruptedException e) {
                        LOGGER.info("Ended prematurely");
                        return; // Ended prematurely
                    }
                }
            }

            if (callBack != null && callBackDelay > 0) {
                try {
                    Thread.sleep((long)(callBackDelay / speedup));
                } catch (InterruptedException e) {
                    return; // Ended prematurely
                }
                callBack.apply(null);
            }
        };
		executeAgent = new Thread(running);
		executeAgent.start();

		if (tasks.isEmpty()) {
			LOGGER.info("Nothing to run");
			return callBackDelay;
		} else {
			return tasks.getLast().getTime() + callBackDelay;
		}
	}

	synchronized void halt() {
		if (isRunning) {
			if (Thread.currentThread() != executeAgent) {
				while (executeAgent.isAlive()) {
					executeAgent.interrupt();
				}
			}

			isRunning = false;
		} else {
			LOGGER.warning("Failed attempting to halt scheduler while not running!");
		}
	}

	synchronized boolean clearTasks() {
		if (isRunning) {
			LOGGER.info("Stop task scheduler first before clearing tasks");
			return false;
		}

		tasks.clear();
		return true;
	}

	@Override
	protected synchronized boolean isLegalAddTask() {
		if (isRunning) {
			LOGGER.warning("Failed attempt to add task to scheduler while running.");
			return false;
		}
		return true;
	}
}
