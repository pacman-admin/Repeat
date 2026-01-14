package core.webui.server.handlers.renderedobjects;

import java.util.List;
import java.util.stream.Collectors;

import utilities.StringUtil;

public final class RenderedPossibleManuallyBuildActions {
	private List<String> actions;

	public static RenderedPossibleManuallyBuildActions of(List<String> possibleActions) {
		RenderedPossibleManuallyBuildActions result = new RenderedPossibleManuallyBuildActions();
		result.actions = possibleActions.stream().map(StringUtil::title).collect(Collectors.toList());
		return result;
	}

	public List<String> getActions() {
		return actions;
	}
}
