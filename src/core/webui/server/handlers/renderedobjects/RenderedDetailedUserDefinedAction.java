package core.webui.server.handlers.renderedobjects;

import core.keyChain.TaskActivationConstructor;
import core.userDefinedTask.UserDefinedAction;

public class RenderedDetailedUserDefinedAction {
	private String id;
	private String name;
	private String isEnabled;
	private String hasPreconditions;
	private RenderedTaskExecutionPreconditions preconditions;
	private RenderedTaskActivation activation;
	private String hasStatistics;
	private RenderedUserDefinedActionStatistics statistics;
	private String hasSourceHistory;
	private RenderedTaskSourceHistory sourceHistory;

	private RenderedDetailedUserDefinedAction() {}

	public static RenderedDetailedUserDefinedAction withEmptyTaskInfo(TaskActivationConstructor activationConstructor) {
		RenderedDetailedUserDefinedAction result = new RenderedDetailedUserDefinedAction();
		result.id = "";
		result.name = "";
		result.isEnabled = "";
		result.hasPreconditions = false + "";
		result.preconditions = null;
		result.activation = RenderedTaskActivation.fromActivation(activationConstructor);
		result.hasStatistics = false + "";
		result.statistics = null;
		result.hasSourceHistory = false + "";
		result.sourceHistory = null;
		return result;
	}

	public static RenderedDetailedUserDefinedAction fromHotkey(String id, String name, TaskActivationConstructor activationConstructor) {
		RenderedDetailedUserDefinedAction result = new RenderedDetailedUserDefinedAction();
		result.id = id;
		result.name = name;
		result.isEnabled = true + "";
		result.hasPreconditions = false + "";
		result.preconditions = null;
		result.activation = RenderedTaskActivation.fromActivation(activationConstructor);
		result.hasStatistics = false + "";
		result.statistics = null;
		result.hasSourceHistory = false + "";
		result.sourceHistory = null;
		return result;
	}

	public static RenderedDetailedUserDefinedAction fromUserDefinedAction(UserDefinedAction action, TaskActivationConstructor activationConstructor) {
		RenderedDetailedUserDefinedAction result = new RenderedDetailedUserDefinedAction();
		result.id = action.getActionId();
		result.name = action.getName();
		result.isEnabled = action.isEnabled() + "";
		result.hasPreconditions = true + "";
		result.preconditions = RenderedTaskExecutionPreconditions.of(action.getExecutionPreconditions());
		result.activation = RenderedTaskActivation.fromActivation(activationConstructor);
		result.hasStatistics = true + "";
		result.statistics = RenderedUserDefinedActionStatistics.fromUserDefinedActionStatistics(action.getStatistics());
		result.hasSourceHistory = true + "";
		result.sourceHistory = RenderedTaskSourceHistory.of(action.getActionId(), action.getTaskSourceHistory());
		return result;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHasPreconditions() {
		return hasPreconditions;
	}
	public void setHasPreconditions(String hasPreconditions) {
		this.hasPreconditions = hasPreconditions;
	}
	public RenderedTaskExecutionPreconditions getPreconditions() {
		return preconditions;
	}
	public void setPreconditions(RenderedTaskExecutionPreconditions preconditions) {
		this.preconditions = preconditions;
	}
	public RenderedTaskActivation getActivation() {
		return activation;
	}
	public void setActivation(RenderedTaskActivation activation) {
		this.activation = activation;
	}
	public String getHasStatistics() {
		return hasStatistics;
	}
	public void setHasStatistics(String hasStatistics) {
		this.hasStatistics = hasStatistics;
	}
	public RenderedUserDefinedActionStatistics getStatistics() {
		return statistics;
	}
	public void setStatistics(RenderedUserDefinedActionStatistics statistics) {
		this.statistics = statistics;
	}
	public String getHasSourceHistory() {
		return hasSourceHistory;
	}
	public RenderedTaskSourceHistory getSourceHistory() {
		return sourceHistory;
	}
	public String getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}
}
