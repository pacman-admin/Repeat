package globalListener;

import org.simplenativehooks.NativeKeyHook;
import org.simplenativehooks.NativeMouseHook;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;
import org.simplenativehooks.listeners.AbstractGlobalMouseListener;

public final class GlobalListenerFactory {
    /**
    This class is uninstantiable
     */
    private GlobalListenerFactory() {}
    public static AbstractGlobalKeyListener createGlobalKeyListener() {
        return NativeKeyHook.of();
    }

    public static AbstractGlobalMouseListener createGlobalMouseListener() {
        return NativeMouseHook.of();
    }
}