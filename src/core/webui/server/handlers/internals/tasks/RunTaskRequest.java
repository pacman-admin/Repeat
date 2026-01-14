package core.webui.server.handlers.internals.tasks;

import utilities.json.AutoJsonable;

@SuppressWarnings("unused")
public final class RunTaskRequest extends AutoJsonable {
	private String id;
	private RunConfig runConfig;

	static RunTaskRequest of() {
		return new RunTaskRequest();
	}

	String getId() {
		return id;
	}

	RunConfig getRunConfig() {
		return runConfig;
	}

	protected static final class RunConfig extends AutoJsonable {
		private String repeatCount;
		private String delayMsBetweenRepeat;

		public static RunConfig of() {
			return new RunConfig();
		}

		public static RunConfig of(String repeatCount, String delayMsBetweenRepeat) {
			RunConfig result =  new RunConfig();
			result.repeatCount = repeatCount;
			result.delayMsBetweenRepeat = delayMsBetweenRepeat;
			return result;
		}

		String getRepeatCount() {
			return repeatCount;
		}

		String getDelayMsBetweenRepeat() {
			return delayMsBetweenRepeat;
		}
	}
}
