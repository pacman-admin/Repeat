package core.keyChain.managers;

import core.config.Constants;
import core.keyChain.ActionInvoker;
import core.keyChain.ButtonStroke;
import core.keyChain.ButtonStroke.Source;
import core.keyChain.KeySequence;
import core.keyChain.RollingKeySeries;
import core.userDefinedTask.UserDefinedAction;
import frontEnd.Backend;

import java.util.*;

public final class KeySequenceManager extends KeyStrokeManager {

    private final RollingKeySeries currentKeyboardRollingKeySeries;
    private final RollingKeySeries currentRollingKeySeries;
    private final List<UserDefinedAction> registeredActions;

    public KeySequenceManager() {
        this.currentKeyboardRollingKeySeries = new RollingKeySeries();
        this.currentRollingKeySeries = new RollingKeySeries();
        this.registeredActions = new ArrayList<>();
    }

    @Override
    public synchronized Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke) {
        if (stroke.getSource() == Source.KEYBOARD) {
            currentKeyboardRollingKeySeries.addKeyStroke(stroke);
        }
        currentRollingKeySeries.addKeyStroke(stroke);
        if (!Backend.CONFIG.isExecuteOnKeyReleased()) {
            return considerTaskExecution(stroke);
        }

        return Collections.emptySet();
    }

    @Override
    public synchronized Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke) {
        if (Backend.CONFIG.isExecuteOnKeyReleased()) {
            return considerTaskExecution(stroke);
        }

        return Collections.emptySet();
    }

    /**
     * Given a new key stroke coming in, consider start executing actions based on their activations.
     *
     * @return set of actions to execute.
     */
    private Set<UserDefinedAction> considerTaskExecution(ButtonStroke key) {
        if (key.getKey() == Constants.HALT_TASK && Backend.CONFIG.isEnabledHaltingKeyPressed()) {
            clear();
            return Collections.emptySet();
        }

        if (key.getSource() == Source.KEYBOARD && key.equals(currentKeyboardRollingKeySeries.getLast())) {
            Set<UserDefinedAction> toExecute = tasksToExecute(currentKeyboardRollingKeySeries);
            if (!toExecute.isEmpty()) {
                return toExecute;
            }
        }

        if (key.equals(currentRollingKeySeries.getLast())) {
            return tasksToExecute(currentRollingKeySeries);
        }
        return Collections.emptySet();
    }

    private Set<UserDefinedAction> tasksToExecute(RollingKeySeries keySeries) {
        Set<UserDefinedAction> output = new HashSet<>();
        for (UserDefinedAction action : registeredActions) {
            ActionInvoker activation = action.getActivation();
            for (KeySequence sequence : activation.getKeySequences()) {
                if (keySeries.collideWith(sequence)) {
                    action.setInvoker(ActionInvoker.newBuilder().withKeySequence(sequence.clone()).build());
                    output.add(action);
                }
            }
        }

        return output;
    }

    @Override
    public synchronized void clear() {
        currentKeyboardRollingKeySeries.clearKeys();
        currentRollingKeySeries.clearKeys();
    }

    @Override
    public Set<UserDefinedAction> collision(Collection<ActionInvoker> activations) {
        Set<UserDefinedAction> output = new HashSet<>();
        for (ActionInvoker activation : activations) {
            for (UserDefinedAction action : registeredActions) {
                if (collisionWithAction(action, activation)) {
                    output.add(action);
                }
            }
        }
        return output;
    }

    private boolean collisionWithAction(UserDefinedAction action, ActionInvoker activation) {
        for (KeySequence sequence : activation.getKeySequences()) {
            for (KeySequence actionSequence : action.getActivation().getKeySequences()) {
                if (actionSequence.collideWith(sequence)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<UserDefinedAction> registerAction(UserDefinedAction action) {
        Set<UserDefinedAction> toRemove = collision(action.getActivation());
        toRemove.forEach(this::unRegisterAction);

        registeredActions.add(action);
        return toRemove;
    }

    @Override
    public Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
        Set<UserDefinedAction> output = new HashSet<>();
        for (Iterator<UserDefinedAction> iterator = registeredActions.iterator(); iterator.hasNext(); ) {
            UserDefinedAction existing = iterator.next();
            if (existing.equals(action)) {
                output.add(existing);
                iterator.remove();
            }
        }
        return output;
    }
}
