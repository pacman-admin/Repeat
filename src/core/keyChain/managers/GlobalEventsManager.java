/**
 * Copyright 2025 Langdon Staab
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Langdon Staab
 * @author HP Truong
 */
package core.keyChain.managers;

import core.keyChain.ActionInvoker;
import core.keyChain.ActivationEvent;
import core.keyChain.KeyStroke;
import core.keyChain.MouseKey;
import core.userDefinedTask.UserDefinedAction;
import core.userDefinedTask.internals.preconditions.ExecutionPreconditionsChecker;
import globalListener.GlobalListenerFactory;
import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.events.NativeMouseEvent;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;
import org.simplenativehooks.listeners.AbstractGlobalMouseListener;
import org.simplenativehooks.utilities.Function;
import utilities.StringUtil;

import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static core.config.Constants.HALT_TASK;
import static main.Main.ACTION_EXECUTOR;
import static main.Main.CONFIG;

public final class GlobalEventsManager {

    private static final Logger LOGGER = Logger.getLogger(GlobalEventsManager.class.getName());

    private final ExecutionPreconditionsChecker executionPreconditionsChecker;
    private final ActivationEventManager taskActivationManager;

    public GlobalEventsManager() {
        this.executionPreconditionsChecker = ExecutionPreconditionsChecker.of();
        this.taskActivationManager = new AggregateActivationEventManager(new KeyChainManager(), new KeySequenceManager(), new PhraseManager(), new MouseGestureManager(), new SharedVariablesManager(), new GlobalKeyActionManager());
    }

    /**
     * Show a short notice that collision occurred.
     *
     * @param collisions set of colliding tasks.
     */
    public static void showCollisionWarning(Set<UserDefinedAction> collisions) {
        String taskNames = StringUtil.join(new Function<UserDefinedAction, String>() {
            @Override
            public String apply(UserDefinedAction d) {
                return '\'' + d.getName() + '\'';
            }
        }.map(collisions), ", ");

        LOGGER.warning("Newly registered keychains " + "will collide with previously registered task(s) " + taskNames + "\n" + "You cannot assign this key chain unless you remove the conflicting key chain...");
    }

    public void startGlobalListener() {
        AbstractGlobalKeyListener keyListener = getAbstractGlobalKeyListener();

        AbstractGlobalMouseListener mouseListener = GlobalListenerFactory.createGlobalMouseListener();
        mouseListener.setMousePressed(new Function<>() {
            @Override
            public Boolean apply(NativeMouseEvent r) {
                MouseKey stroke = MouseKey.of(r);

                Set<UserDefinedAction> actions = taskActivationManager.onActivationEvent(ActivationEvent.of(stroke));
                actions = actions.stream().filter(executionPreconditionsChecker::shouldExecute).collect(Collectors.toSet());
                ACTION_EXECUTOR.startExecutingActions(actions);
                return true;
            }
        });
        mouseListener.setMouseReleased(new Function<>() {
            @Override
            public Boolean apply(NativeMouseEvent r) {
                MouseKey stroke = MouseKey.of(r);

                Set<UserDefinedAction> actions = taskActivationManager.onActivationEvent(ActivationEvent.of(stroke));
                actions = actions.stream().filter(executionPreconditionsChecker::shouldExecute).collect(Collectors.toSet());
                ACTION_EXECUTOR.startExecutingActions(actions);
                return true;
            }
        });

//        SharedVariablesPubSubManager.get().addSubscriber(SharedVariablesSubscriber.of(SharedVariablesSubscription.forAll(), e -> {
//            Set<UserDefinedAction> actions = taskActivationManager.onActivationEvent(ActivationEvent.of(e));
//            actions = actions.stream().filter(executionPreconditionsChecker::shouldExecute).collect(Collectors.toSet());
//            actionExecutor.startExecutingActions(actions);
//        }));

        taskActivationManager.startListening();
        keyListener.startListening();
        mouseListener.startListening();
    }

    private AbstractGlobalKeyListener getAbstractGlobalKeyListener() {
        AbstractGlobalKeyListener keyListener = GlobalListenerFactory.createGlobalKeyListener();
        keyListener.setKeyPressed(new Function<>() {
            @Override
            public Boolean apply(NativeKeyEvent r) {
                KeyStroke stroke = KeyStroke.of(r);
                LOGGER.fine("Key pressed " + stroke);

                if (!shouldDelegate(stroke)) {
                    return true;
                }

                Set<UserDefinedAction> actions = taskActivationManager.onActivationEvent(ActivationEvent.of(stroke));
                actions = actions.stream().filter(executionPreconditionsChecker::shouldExecute).collect(Collectors.toSet());
                ACTION_EXECUTOR.startExecutingActions(actions);
                return true;
            }
        });

        keyListener.setKeyReleased(new Function<>() {
            @Override
            public Boolean apply(NativeKeyEvent r) {
                KeyStroke stroke = KeyStroke.of(r);
                LOGGER.fine("Key released " + stroke);
                if (!shouldDelegate(stroke)) {
                    return true;
                }

                Set<UserDefinedAction> actions = taskActivationManager.onActivationEvent(ActivationEvent.of(stroke));
                actions = actions.stream().filter(executionPreconditionsChecker::shouldExecute).collect(Collectors.toSet());
                ACTION_EXECUTOR.startExecutingActions(actions);
                return true;
            }
        });
        return keyListener;
    }

    /**
     * Given a new key code coming in, consider whether we should delegate
     * to the {@link KeyStrokeManager}, or take actions and terminate.
     *
     * @return if we should continue delegating this to the managers.
     */
    private boolean shouldDelegate(KeyStroke stroke) {
        if ((stroke.getKey() == HALT_TASK) && CONFIG.isEnabledHaltingKeyPressed()) {
            taskActivationManager.clear();
            ACTION_EXECUTOR.haltAllTasks();
            return false;
        }
        return true;
    }

    /**
     * Map all key chains of the current task to the action. Kick out all colliding tasks.
     *
     * @param action action to register.
     */
    public void registerTask(UserDefinedAction action) {
        taskActivationManager.registerAction(action);
    }

    /**
     * Unregister the action, then modify the action activation to be the new activation, and finally register the modified action.
     * This kicks out all other actions that collide with the action provided.
     *
     * @param action        action to be re-registered with new activation.
     * @param newActivation new activation to be associated with the action.
     */
    public void reRegisterTask(UserDefinedAction action, ActionInvoker newActivation) {
        unregisterTask(action);
        action.setActivation(newActivation);
        registerTask(action);
    }

    /**
     * Remove all bindings to the task's activation.
     *
     * @param action action whose activation will be removed.
     */
    public void unregisterTask(UserDefinedAction action) {
        taskActivationManager.unRegisterAction(action);
    }

    /**
     * @param action
     * @return return set of actions that collide with this action, excluding the input task.
     */
    public Set<UserDefinedAction> isTaskRegistered(UserDefinedAction action) {
        Set<UserDefinedAction> output = isActivationRegistered(action.getActivation());
        output.remove(action);
        return output;
    }

    /**
     * Check if an activation is already registered.
     *
     * @param activation
     * @return return set of actions that collide with this activation.
     */
    public Set<UserDefinedAction> isActivationRegistered(ActionInvoker activation) {
        return taskActivationManager.collision(activation);
    }


    public void shutdown() {
        taskActivationManager.stopListening();
        ACTION_EXECUTOR.haltAllTasks();

    }
}
