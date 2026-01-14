package core.keyChain.managers;

import core.keyChain.ActionInvoker;
import core.keyChain.ButtonStroke;
import core.keyChain.KeyChain;
import core.userDefinedTask.UserDefinedAction;

import java.util.*;

public final class GlobalKeyActionManager extends KeyStrokeManager {

    private final Set<UserDefinedAction> onKeyStrokePressedTasks;
    private final Set<UserDefinedAction> onKeyReleasedTasks;

    public GlobalKeyActionManager() {

        onKeyStrokePressedTasks = new HashSet<>();
        onKeyReleasedTasks = new HashSet<>();
    }

    @Override
    public Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke) {
        for (UserDefinedAction action : onKeyStrokePressedTasks) {
            action.setInvoker(ActionInvoker.newBuilder().withHotKey(new KeyChain(List.of(stroke))).build());
        }

        return new HashSet<>(onKeyStrokePressedTasks);
    }

    @Override
    public Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke) {
        for (UserDefinedAction action : onKeyReleasedTasks) {
            action.setInvoker(ActionInvoker.newBuilder().withHotKey(new KeyChain(List.of(stroke))).build());
        }

        return new HashSet<>(onKeyReleasedTasks);
    }

    @Override
    public void clear() {
        // Nothing to do.
    }

    @Override
    public Set<UserDefinedAction> collision(Collection<ActionInvoker> activations) {
        return new HashSet<>();
    }

    @Override
    public Set<UserDefinedAction> registerAction(UserDefinedAction action) {
        if (action.getActivation().getGlobalActivation().isOnKeyPressed()) {
            onKeyStrokePressedTasks.add(action);
        }
        if (action.getActivation().getGlobalActivation().isOnKeyReleased()) {
            onKeyReleasedTasks.add(action);
        }
        return new HashSet<>();
    }

    @Override
    public Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
        Set<UserDefinedAction> removed = new HashSet<>();
        for (Iterator<UserDefinedAction> it = onKeyStrokePressedTasks.iterator(); it.hasNext(); ) {
            UserDefinedAction pressed = it.next();
            if (pressed.equals(action)) {
                removed.add(pressed);
                it.remove();
            }
        }
        for (Iterator<UserDefinedAction> it = onKeyReleasedTasks.iterator(); it.hasNext(); ) {
            UserDefinedAction released = it.next();
            if (released.equals(action)) {
                removed.add(released);
                it.remove();
            }
        }
        return removed;
    }

}
