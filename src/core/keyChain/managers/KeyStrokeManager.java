package core.keyChain.managers;

import core.keyChain.ActivationEvent;
import core.keyChain.ActivationEvent.EventType;
import core.keyChain.ButtonStroke;
import core.userDefinedTask.UserDefinedAction;

import java.util.HashSet;
import java.util.Set;

public abstract class KeyStrokeManager implements ActivationEventManager {
    private boolean listening = false;

    @Override
    public void startListening() {
        listening = true;
    }

    @Override
    public void stopListening() {
        listening = false;
    }

    protected final boolean isListening() {
        return listening;
    }

    protected final boolean isIgnoring() {
        return !listening;
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

    protected abstract Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke);

    protected abstract Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke);
}
