package core.keyChain.managers;

import core.config.Config;
import core.config.Constants;
import core.keyChain.ActionInvoker;
import core.keyChain.ButtonStroke;
import core.keyChain.ButtonStroke.Source;
import core.keyChain.KeyChain;
import core.userDefinedTask.UserDefinedAction;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyChainManager extends KeyStrokeManager {

    private final Set<ButtonStroke> pressedKeyboardKeys, pressedKeys;
    private final Map<KeyChain, UserDefinedAction> keyChainActions;
    private final KeyChain currentKeyboardChain;
    private final KeyChain currentKeyChain;
    private UserDefinedAction pendingAction;

    public KeyChainManager(Config config) {
        super(config);
        currentKeyboardChain = new KeyChain();
        currentKeyChain = new KeyChain();
        pressedKeyboardKeys = Collections.synchronizedSet(new HashSet<>());
        pressedKeys = Collections.synchronizedSet(new HashSet<>());
        keyChainActions = new HashMap<>();

    }

    @Override
    public void startListening() {
        // Do nothing.
    }

    @Override
    public synchronized Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke) {
        if (stroke.getSource() == Source.KEYBOARD) {
            pressedKeyboardKeys.add(stroke);
        }
        pressedKeys.add(stroke);

        if (stroke.getSource() == Source.KEYBOARD) {
            currentKeyboardChain.addKeyStroke(stroke);
        }
        currentKeyChain.addKeyStroke(stroke);

        UserDefinedAction action = null;
        if (!getConfig().isExecuteOnKeyReleased()) {
            action = considerTaskExecution(stroke);
        }

        return Stream.of(action).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public synchronized Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke) {
        if (stroke.getSource() == Source.KEYBOARD) {
            pressedKeyboardKeys.remove(stroke);
        }
        pressedKeys.remove(stroke);
        UserDefinedAction action = null;
        if (getConfig().isExecuteOnKeyReleased()) {
            action = considerTaskExecution(stroke);
        }

        if (stroke.getSource() == Source.KEYBOARD) {
            currentKeyboardChain.clearKeys();
        }
        currentKeyChain.clearKeys();

        if (action != null) {
            pendingAction = action;
        }
        if (pressedKeyboardKeys.isEmpty() || pressedKeys.isEmpty()) {
            UserDefinedAction toExecute = pendingAction;
            pendingAction = null;
            return Stream.of(toExecute).filter(Objects::nonNull).collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

    @Override
    public void clear() {
        currentKeyboardChain.clearKeys();
        currentKeyChain.clearKeys();
    }

    @Override
    public Set<UserDefinedAction> collision(Collection<ActionInvoker> activations) {
        Set<KeyChain> keyChains = activations.stream().map(ActionInvoker::getHotkeys).flatMap(Set::stream).collect(Collectors.toSet());

        Set<UserDefinedAction> collisions = new HashSet<>();
        for (Entry<KeyChain, UserDefinedAction> entry : keyChainActions.entrySet()) {
            KeyChain existing = entry.getKey();
            UserDefinedAction action = entry.getValue();

            for (KeyChain key : keyChains) {
                if (existing.collideWith(key)) {
                    collisions.add(action);
                }
            }
        }
        return collisions;
    }

    @Override
    public Set<UserDefinedAction> registerAction(UserDefinedAction action) {
        Set<UserDefinedAction> toRemove = collision(action.getActivation());
        toRemove.forEach(this::unRegisterAction);

        for (KeyChain key : action.getActivation().getHotkeys()) {
            keyChainActions.put(key, action);
        }

        return toRemove;
    }

    @Override
    public Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
        return action.getActivation().getHotkeys().stream().map(keyChainActions::remove).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * Given a new key code coming in, consider start executing an action based on its hotkey
     *
     * @param stroke new keyCode coming in
     * @return if operation succeeded (even if no action has been invoked)
     */
    private UserDefinedAction considerTaskExecution(ButtonStroke stroke) {
        if (stroke.getKey() == Constants.HALT_TASK && getConfig().isEnabledHaltingKeyPressed()) {
            clear();
            return null;
        }

        if (stroke.getSource() == Source.KEYBOARD) {
            UserDefinedAction action = keyChainActions.get(currentKeyboardChain);
            if (action != null) {
                action.setInvoker(ActionInvoker.newBuilder().withHotKey(currentKeyboardChain.clone()).build());
            }
            return action;
        }

        UserDefinedAction action = keyChainActions.get(currentKeyChain);
        if (action != null) {
            action.setInvoker(ActionInvoker.newBuilder().withHotKey(currentKeyChain.clone()).build());
        }

        return action;
    }
}
