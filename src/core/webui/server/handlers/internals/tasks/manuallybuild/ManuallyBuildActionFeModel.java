package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.util.*;
import java.util.stream.Collectors;

public final class ManuallyBuildActionFeModel {

    private static final Map<Actor, List<Action>> ACTORS_TO_ACTIONS = new HashMap<>();

    static {
        ACTORS_TO_ACTIONS.put(Actor.MOUSE, Arrays.asList(MouseAction.values()));
        ACTORS_TO_ACTIONS.put(Actor.KEYBOARD, Arrays.asList(KeyboardAction.values()));
        ACTORS_TO_ACTIONS.put(Actor.CONTROLLER, Arrays.asList(ControllerAction.values()));
    }

    private ManuallyBuildActionFeModel() {
    }

    public static ManuallyBuildActionFeModel of() {
        return new ManuallyBuildActionFeModel();
    }

    public List<String> noAction() {
        return new ArrayList<>(0);
    }

    public List<String> actionsForActor(String actorValue) {
        Actor actor = Actor.forValue(actorValue);
        return ACTORS_TO_ACTIONS.getOrDefault(actor, List.of()).stream().map(Action::toString).collect(Collectors.toList());
    }

    public enum Actor {
        MOUSE("mouse"),
        KEYBOARD("keyboard"),
        CONTROLLER("controller");

        private final String value;

        Actor(String s) {
            value = s;
        }

        private static Actor forValue(String s) {
            for (Actor actor : Actor.values()) {
                if (actor.toString().equals(s)) {
                    return actor;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum MouseAction implements Action {
        CLICK("click"),
        CLICK_CURRENT_POSITION("click current position"),
        MOVE_BY("move by"),
        MOVE("move"),
        PRESS_CURRENT_POSITION("press current position"),
        RELEASE_CURRENT_POSITION("release current position");

        private final String value;

        MouseAction(String s) {
            value = s;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum KeyboardAction implements Action {
        PRESS_KEY("press"),
        RELEASE_KEY("release"),
        TYPE_KEY("type"),
        TYPE_STRING_KEY("type string");

        private final String value;

        KeyboardAction(String s) {
            value = s;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum ControllerAction implements Action {
        WAIT("blocking wait");

        private final String value;

        ControllerAction(String s) {
            value = s;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    interface Action {
        @Override
        String toString();
    }
}
