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
import core.keyChain.ButtonStroke;
import core.keyChain.MouseGesture;
import core.keyChain.mouseGestureRecognition.MouseGestureClassifier;
import core.userDefinedTask.UserDefinedAction;
import frontEnd.Backend;
import globalListener.GlobalListenerFactory;
import org.simplenativehooks.events.NativeMouseEvent;
import org.simplenativehooks.listeners.AbstractGlobalMouseListener;
import org.simplenativehooks.utilities.Function;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class to manage mouse gesture recognition and action
 */
public final class MouseGestureManager extends KeyStrokeManager {

    private static final Logger LOGGER = Logger.getLogger(MouseGestureManager.class.getName());

    private static final int MAX_COORDINATES_COUNT = 1000;

    private final MouseGestureClassifier mouseGestureRecognizer;
    private final Map<MouseGesture, UserDefinedAction> actionMap;
    private final AbstractGlobalMouseListener mouseListener;
    private final Queue<Point> coordinates;
    private boolean enabled;

    public MouseGestureManager() {

        mouseGestureRecognizer = new MouseGestureClassifier();
        actionMap = new HashMap<>();
        coordinates = new ConcurrentLinkedQueue<>();
        mouseListener = GlobalListenerFactory.createGlobalMouseListener();
    }

    /**
     * Start listening to the mouse for movement
     */
    @Override
    public void startListening() {
        mouseListener.setMouseMoved(new Function<>() {
            @Override
            public Boolean apply(NativeMouseEvent d) {
                LOGGER.finest("Mouse moved to " + d.getX() + ", " + d.getY() + ".");
                if (enabled && coordinates.size() < MAX_COORDINATES_COUNT) {
                    coordinates.add(new Point(d.getX(), d.getY()));
                }
                return true;
            }
        });
        mouseListener.startListening();
    }

    @Override
    public Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke) {
        if (Backend.CONFIG.getMOUSE_GESTURE().getButtonStrokes().contains(stroke)) {
            startRecording();
        }
        return Collections.emptySet();
    }

    @Override
    public Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke) {
        if (Backend.CONFIG.getMOUSE_GESTURE().getButtonStrokes().contains(stroke)) {
            return finishRecording();
        }
        return Collections.emptySet();
    }

    @Override
    public void clear() {
        enabled = false;
        coordinates.clear();
    }

    /**
     * Check if any collision between the gestures set and the set of currently registered gestures
     *
     * @return set of any collision occurs
     */
    @Override
    public Set<UserDefinedAction> collision(Collection<ActionInvoker> activations) {
        Set<MouseGesture> gestures = activations.stream().map(ActionInvoker::getMouseGestures).flatMap(Set::stream).collect(Collectors.toSet());

        Set<MouseGesture> collisions = new HashSet<>(actionMap.keySet());
        collisions.retainAll(gestures);

        Set<UserDefinedAction> output = new HashSet<>();
        for (MouseGesture collision : collisions) {
            output.add(actionMap.get(collision));
        }
        return output;
    }

    /**
     * Register an action associated with a {@link MouseGesture}.
     *
     * @param action the action to execute
     * @return the gestures that are collided
     */
    @Override
    public Set<UserDefinedAction> registerAction(UserDefinedAction action) {
        Set<UserDefinedAction> collisions = new HashSet<>();
        for (MouseGesture gesture : action.getActivation().getMouseGestures()) {
            UserDefinedAction collided = actionMap.get(gesture);
            if (collided != null) {
                collisions.add(collided);
            }

            actionMap.put(gesture, action);
        }

        return collisions;
    }

    /**
     * Unregister the action associated with a {@link MouseGesture}
     *
     * @param action action to unregister
     * @return action (if exist) associated with this gesture
     */
    @Override
    public Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
        Set<UserDefinedAction> output = new HashSet<>();
        for (MouseGesture gesture : action.getActivation().getMouseGestures()) {
            UserDefinedAction removed = actionMap.remove(gesture);
            if (removed != null) {
                output.add(removed);
            }
        }

        return output;
    }

    /**
     * Start recording the gesture
     */
    private synchronized void startRecording() {
        if (enabled) {
            return;
        }
        coordinates.clear();
        enabled = true;
    }

    /**
     * Finish recording the gesture. Now decode it.
     */
    private synchronized Set<UserDefinedAction> finishRecording() {
        enabled = false;
        try {
            MouseGesture gesture = processCurrentData();
            /*if (MouseGesture.IGNORED_CLASSIFICATIONS.contains(gesture)) {
                return Collections.emptySet();
            }*/

            UserDefinedAction task = actionMap.get(gesture);
            if (task == null) {
                return Collections.emptySet();
            }

            task.setInvoker(ActionInvoker.newBuilder().withMouseGesture(gesture).build());
            return new HashSet<>(List.of(task));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to classify recorded data", e);
        }
        return Collections.emptySet();
    }

    /**
     * Process currently stored points and detect any gesture
     *
     * @return the detected {@link MouseGesture}
     */
    private MouseGesture processCurrentData() {
        int size = coordinates.size();
        return mouseGestureRecognizer.classifyGesture(coordinates, size);
    }

    /**
     * Stop listening to the mouse for movement
     */
    public void stopListening() {
        mouseListener.stopListening();
    }
}
