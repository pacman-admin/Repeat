package core.languageHandler.sourceGenerator;

import java.util.logging.Logger;

import core.scheduler.AbstractScheduler;
import core.scheduler.SchedulingData;
import utilities.Function;

class TaskSourceScheduler extends AbstractScheduler<String> {

	private static final Logger LOGGER = Logger.getLogger(TaskSourceScheduler.class.getName());

	private Function<Long, String> getSleepSource;

	TaskSourceScheduler() {
		super();
	}

	/**
	 * Generate the source code from scheduling data.
	 * Note that the speedup only changes the amount of delay between scheduled actions.
	 *
	 * @param speedup speedup for the task.
	 * @return the generated source code as a string.
	 */
    String getSource(float speedup) {
		if (!isLegalAddTask()) {
			return null;
		}

		StringBuffer output = new StringBuffer();

		long time = 0;
		for (SchedulingData<String> t : tasks) {
			long currentTime = t.getTime();

			if (currentTime < time) {
				LOGGER.severe("Something went really bad");
				System.exit(1);
			}

			output.append(getSleepSource.apply((long) ((currentTime - time) / speedup)) + "\n");

			time = currentTime;
			output.append(t.getData());
		}

		return output.toString();
	}

	@Override
	protected boolean isLegalAddTask() {
		if (getSleepSource == null) {
			LOGGER.severe("Unable to generate source. Function getSleepSource is null.");
			return false;
		}
		return true;
	}

	void setSleepSource(Function<Long, String> getSleepSource) {
		this.getSleepSource = getSleepSource;
	}
}
