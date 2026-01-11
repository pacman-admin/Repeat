package core.keyChain;

import core.userDefinedTask.internals.SharedVariablesEvent;

public final class ActivationEvent {
    private final EventType type;
    private ButtonStroke buttonStroke;
    private SharedVariablesEvent variable;

    private ActivationEvent(ButtonStroke buttonStroke) {
        this.type = EventType.BUTTON_STROKE;
        this.buttonStroke = buttonStroke;
    }

    private ActivationEvent(SharedVariablesEvent variable) {
        this.type = EventType.SHARED_VARIABLE;
        this.variable = variable;
    }

    public static ActivationEvent of(ButtonStroke buttonStroke) {
        return new ActivationEvent(buttonStroke);
    }

    public static ActivationEvent of(SharedVariablesEvent variable) {
        return new ActivationEvent(variable);
    }

    public EventType getType() {
        return type;
    }

    public ButtonStroke getButtonStroke() {
        if (type != EventType.BUTTON_STROKE) {
            throw new RuntimeException("This Action refers not to a ButtonStroke, but to a " + type);
        }
        return buttonStroke;
    }

    public SharedVariablesEvent getVariable() {
        if (type != EventType.SHARED_VARIABLE) {
            throw new RuntimeException("This Action refers not to a SharedVariableEvent, but to a " + type + ".");
        }
        return variable;
    }

    public enum EventType {
        BUTTON_STROKE("button_stroke"), SHARED_VARIABLE("shared_variable");

        private final String value;

        EventType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
