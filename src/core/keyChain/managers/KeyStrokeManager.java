package core.keyChain.managers;

import core.config.Config;
import core.keyChain.ActivationEvent;
import core.keyChain.ActivationEvent.EventType;
import core.keyChain.ButtonStroke;
import core.userDefinedTask.UserDefinedAction;

import java.util.HashSet;
import java.util.Set;

public abstract class KeyStrokeManager extends ActivationEventManager {
    private final Config config;

    KeyStrokeManager(Config config) {
        this.config = config;
    }

    final Config getConfig() {
        return config;
    }

    @Override
    public final Set<UserDefinedAction> onActivationEvent(ActivationEvent event) {
        if (event.getType() != EventType.BUTTON_STROKE) {
            return new HashSet<>();
        }

        ButtonStroke buttonStroke = event.getButtonStroke();
        if (buttonStroke.isPressed()) {
            return onButtonStrokePressed(buttonStroke);
        }
        return onButtonStrokeReleased(buttonStroke);
    }

    @Override
    public void stopListening() {
        //Nothing to do.
    }

    protected abstract Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke);

    protected abstract Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke);
}
