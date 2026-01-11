package core.webui.server.handlers.renderedobjects;

import core.keyChain.ActionInvoker;

@SuppressWarnings("unused")
public final class RenderedGlobalActivation {
    private final boolean onKeyPressed;
    private final boolean onKeyReleased;

    private RenderedGlobalActivation(boolean onKeyPressed, boolean onKeyReleased) {
        this.onKeyPressed = onKeyPressed;
        this.onKeyReleased = onKeyReleased;
    }

    public static RenderedGlobalActivation fromActivation(ActionInvoker activation) {
        return new RenderedGlobalActivation(activation.getGlobalActivation().isOnKeyPressed(), activation.getGlobalActivation().isOnKeyReleased());
    }

    public boolean isOnKeyPressed() {
        return onKeyPressed;
    }

    public boolean isOnKeyReleased() {
        return onKeyReleased;
    }
}
