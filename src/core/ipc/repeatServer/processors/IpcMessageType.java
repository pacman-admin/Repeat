package core.ipc.repeatServer.processors;

public enum IpcMessageType {
	ACTION("action"),
	TASK("task"),
	SHARED_MEMORY("shared_memory"),
	SYSTEM_HOST("system_host"),
	SYSTEM_CLIENT("system_client");

	private final String value;

	private IpcMessageType(String value) {
		this.value = value;
	}

	String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

	static IpcMessageType identify(String value) {
		for (IpcMessageType type : IpcMessageType.values()) {
			if (type.value.equals(value)) {
				return type;
			}
		}

		return null;
	}
}
