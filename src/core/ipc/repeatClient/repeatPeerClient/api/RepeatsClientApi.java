package core.ipc.repeatClient.repeatPeerClient.api;

import core.ipc.repeatClient.repeatPeerClient.RepeatPeerServiceClientWriter;

public class RepeatsClientApi {

	private RepeatsActionsApi actions;
	private RepeatsMouseControllerApi mouse;
	private RepeatsKeyboardControllerApi keyboard;
	private RepeatsToolApi tool;

	public RepeatsClientApi(RepeatPeerServiceClientWriter repeatPeerServiceClientWriter) {
		actions = new RepeatsActionsApi(repeatPeerServiceClientWriter);
		mouse = new RepeatsMouseControllerApi(repeatPeerServiceClientWriter);
		keyboard = new RepeatsKeyboardControllerApi(repeatPeerServiceClientWriter);
		tool = new RepeatsToolApi(repeatPeerServiceClientWriter);
	}

	public RepeatsActionsApi actions() {
		return actions;
	}

	public RepeatsMouseControllerApi mouse() {
		return mouse;
	}

	public RepeatsKeyboardControllerApi keyboard() {
		return keyboard;
	}

	public RepeatsToolApi tool() {
		return tool;
	}
}
