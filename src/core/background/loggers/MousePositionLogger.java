package core.background.loggers;

import core.controller.Core;
import core.controller.CoreProvider;
import globalListener.GlobalListenerFactory;
import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;
import org.simplenativehooks.utilities.Function;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * Logs the mouse position whenever the left Control key is pressed.
 */
public final class MousePositionLogger {

    private static final Logger LOGGER = Logger.getLogger(MousePositionLogger.class.getName());

    private final AbstractGlobalKeyListener keyListener;
    private boolean enabled;

    public MousePositionLogger(CoreProvider coreProvider) {
        Core controller = coreProvider.getLocal();
        keyListener = GlobalListenerFactory.of().createGlobalKeyListener();
        keyListener.setKeyPressed(new Function<>() {
            @Override
            public Boolean apply(NativeKeyEvent e) {
                if (!enabled) {
                    return Boolean.TRUE;
                }
                if (e.getKey() != KeyEvent.VK_CONTROL || e.getModifier() != NativeKeyEvent.Modifier.KEY_MODIFIER_LEFT) {
                    return Boolean.TRUE;
                }

                Point p = controller.mouse().getPosition();
                LOGGER.info("Mouse position: " + p.getX() + ", " + p.getY());
                return Boolean.TRUE;
            }
        });
        keyListener.startListening();
    }

    public void stop() {
        keyListener.stopListening();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
