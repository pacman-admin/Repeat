package core.webui.server.handlers.renderedobjects;

import core.userDefinedTask.UserDefinedAction;
import utilities.DateUtility;

public class RenderedUserDefinedAction {

	private String id;
	private String name;
	private String activation;
	private String enabled;
	private long useCount;
	private String lastUsed;

	public static RenderedUserDefinedAction fromUserDefinedAction(UserDefinedAction action) {
		RenderedUserDefinedAction output = new RenderedUserDefinedAction();
		output.setId(action.getActionId());
		output.setName(action.getName());
		String representative = action.getActivation().getRepresentativeString();
		String activation = "None";
		if (representative != null && !representative.isEmpty()) {
			activation = representative;
		}
		output.setActivation(activation);
		output.setEnabled(action.isEnabled() + "");
		output.setUseCount(action.getStatistics().getCount());

		String lastUsed = DateUtility.calendarToDateString(action.getStatistics().getLastUse());
		output.setLastUsed(lastUsed == null ? "" : lastUsed);
		return output;
	}


	public String getId() {
		return id;
	}
	private void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getActivation() {
		return activation;
	}

	private void setActivation(String activation) {
		this.activation = activation;
	}

	public String getEnabled() {
		return enabled;
	}

	private void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public long getUseCount() {
		return useCount;
	}

	private void setUseCount(long useCount) {
		this.useCount = useCount;
	}

	public String getLastUsed() {
		return lastUsed;
	}

	private void setLastUsed(String lastUsed) {
		this.lastUsed = lastUsed;
	}
}
