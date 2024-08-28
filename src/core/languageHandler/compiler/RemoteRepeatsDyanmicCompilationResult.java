package core.languageHandler.compiler;

import java.util.Map;

import core.userDefinedTask.UserDefinedAction;

public final class RemoteRepeatsDyanmicCompilationResult extends DynamicCompilationResult {

	private final Map<String, String> clientIdToActionId;

	private RemoteRepeatsDyanmicCompilationResult(DynamicCompilerOutput output, UserDefinedAction action, Map<String, String> clientIdToActionId) {
		super(output, action);
		this.clientIdToActionId = clientIdToActionId;
	}

	public static RemoteRepeatsDyanmicCompilationResult of(DynamicCompilerOutput output, UserDefinedAction action, Map<String, String> clientIdToActionId) {
		return new RemoteRepeatsDyanmicCompilationResult(output, action, clientIdToActionId);
	}

	public Map<String, String> clientIdToActionId() {
		return clientIdToActionId;
	}
}
