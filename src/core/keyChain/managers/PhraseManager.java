package core.keyChain.managers;

import core.config.Constants;
import core.keyChain.ActionInvoker;
import core.keyChain.ActivationPhrase;
import core.keyChain.ButtonStroke;
import core.keyChain.ButtonStroke.Source;
import core.keyChain.RollingKeySeries;
import core.userDefinedTask.UserDefinedAction;
import frontEnd.Backend;

import java.util.*;

public final class PhraseManager extends KeyStrokeManager {

    private final RollingKeySeries currentRollingKeySeries;
    private final List<UserDefinedAction> registeredActions;

    public PhraseManager() {
        this.currentRollingKeySeries = new RollingKeySeries();
        this.registeredActions = new ArrayList<>();
    }

    @Override
    public synchronized Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke) {
        if (isListening() && stroke.getSource() == Source.KEYBOARD) {
            currentRollingKeySeries.addKeyStroke(stroke);
            if (!Backend.CONFIG.isExecuteOnKeyReleased()) {
                return considerTaskExecution(stroke);
            }
        }
        return Collections.emptySet();
    }

    @Override
    public synchronized Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke) {
        if (isListening() && stroke.getSource() == Source.KEYBOARD) {
            currentRollingKeySeries.addKeyStroke(stroke);
            if (Backend.CONFIG.isExecuteOnKeyReleased()) {
                return considerTaskExecution(stroke);
            }
        }
        return Collections.emptySet();
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
        for (ActivationPhrase phrase : activation.getPhrases()) {
            for (ActivationPhrase actionPhrase : action.getActivation().getPhrases()) {
                if (phrase.collideWith(actionPhrase)) {
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

    @Override
    public synchronized void clear() {
        currentRollingKeySeries.clearKeys();
    }

    /**
     * Given a new key stroke coming in, consider start executing actions based on their activations.
     *
     * @return set of actions to execute.
     */
    private Set<UserDefinedAction> considerTaskExecution(ButtonStroke key) {
        if (key.getKey() == Constants.HALT_TASK || !isIgnoring()) {
            clear();
            return Collections.emptySet();
        }

        if (key.equals(currentRollingKeySeries.getLast())) {
            return tasksToExecute();
        }
        return Collections.emptySet();
    }

    private Set<UserDefinedAction> tasksToExecute() {
        Set<UserDefinedAction> output = new HashSet<>();
        for (UserDefinedAction action : registeredActions) {
            ActionInvoker activation = action.getActivation();
            for (ActivationPhrase phrase : activation.getPhrases()) {
                if (currentRollingKeySeries.collideWith(phrase)) {
                    action.setInvoker(ActionInvoker.newBuilder().withPhrase(phrase.clone()).build());
                    output.add(action);
                }
            }
        }

        return output;
    }
}
