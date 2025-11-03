package core.ipc.repeatClient.repeatPeerClient.api;

import core.ipc.repeatClient.repeatPeerClient.RepeatPeerServiceClientWriter;

@SuppressWarnings("unused")
public class RepeatsClientApi {

    private final RepeatsActionsApi actions;
    private final RepeatsMouseControllerApi mouse;
    private final RepeatsKeyboardControllerApi keyboard;
    private final RepeatsToolApi tool;

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
