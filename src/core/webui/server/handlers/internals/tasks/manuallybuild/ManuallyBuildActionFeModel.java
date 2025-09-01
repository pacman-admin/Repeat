package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManuallyBuildActionFeModel {

	public static ManuallyBuildActionFeModel of() {
		return new ManuallyBuildActionFeModel();
	}

	private ManuallyBuildActionFeModel() {}

	public enum Actor {
		MOUSE("mouse"),
		KEYBOARD("keyboard"),
		CONTROLLER("controller")
		;

		private String value;
		Actor(String s) { value = s; }
		@Override
		public String toString() {
			return value;
		}

		static Actor forValue(String s) {
			for (Actor actor : Actor.values()) {
				if (actor.toString().equals(s)) {
					return actor;
				}
			}
			return null;
		}
	}

	private static final Map<Actor, List<Action>> ACTORS_TO_ACTIONS = new HashMap<>();
	static {
		ACTORS_TO_ACTIONS.put(Actor.MOUSE, Arrays.asList(MouseAction.values()));
		ACTORS_TO_ACTIONS.put(Actor.KEYBOARD, Arrays.asList(KeyboardAction.values()));
		ACTORS_TO_ACTIONS.put(Actor.CONTROLLER, Arrays.asList(ControllerAction.values()));
	}

	interface Action {
		@Override
        String toString();
	}

	public List<String> noAction() {
		return new ArrayList<>(0);
	}

	public List<String> actionsForActor(String actorValue) {
		Actor actor = Actor.forValue(actorValue);
		return ACTORS_TO_ACTIONS.getOrDefault(actor, Arrays.asList()).stream().map(Action::toString).collect(Collectors.toList());
	}

	public enum MouseAction implements Action {
		CLICK("click"),
		CLICK_CURRENT_POSITION("click current position"),
		MOVE_BY("move by"),
		MOVE("move"),
		PRESS_CURRENT_POSITION("press current position"),
		RELEASE_CURRENT_POSITION("release current position")
		;

		private String value;
		MouseAction(String s) { value = s; }
		@Override
		public String toString() {
			return value;
		}
	}

	public enum KeyboardAction implements Action {
		PRESS_KEY("press"),
		RELEASE_KEY("release"),
		TYPE_KEY("type"),
		TYPE_STRING_KEY("type string")
		;

		private String value;
		KeyboardAction(String s) { value = s; }
		@Override
		public String toString() {
			return value;
		}
	}

	public enum ControllerAction implements Action {
		WAIT("blocking wait");

		private String value;
		ControllerAction(String s) { value = s; }
		@Override
		public String toString() {
			return value;
		}
	}
}
