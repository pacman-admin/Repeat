package core.keyChain.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import core.keyChain.ActivationEvent;
import core.keyChain.ActivationEvent.EventType;
import core.keyChain.SharedVariablesActivation;
import core.keyChain.ActionInvoker;
import core.userDefinedTask.UserDefinedAction;
import core.userDefinedTask.internals.SharedVariablesEvent;
import core.userDefinedTask.internals.SharedVariablesSubscription;

public final class SharedVariablesManager implements ActivationEventManager {

	private final List<UserDefinedAction> registeredActions;

	public SharedVariablesManager() {
		registeredActions = new ArrayList<>();
	}

	@Override
	public void startListening() {
		// Nothing to do.
	}

	@Override
	public void stopListening() {
		// Nothing to do.
	}

	@Override
	public void clear() {
		// Nothing to do.
	}

	@Override
	public Set<UserDefinedAction> onActivationEvent(ActivationEvent event) {
		if (event.getType() != EventType.SHARED_VARIABLE) {
			return new HashSet<>();
		}

		SharedVariablesEvent variable = event.getVariable();
		Set<UserDefinedAction> output = new HashSet<>();
		for (UserDefinedAction action : registeredActions) {
			for (SharedVariablesActivation variableActivation : action.getActivation().getVariables()) {
				if (variableActivation.getVariable().includes(variable)) {
					action.setInvoker(ActionInvoker.newBuilder().withVariable(SharedVariablesActivation.of(SharedVariablesSubscription.forVar(variable.getNamespace(),  variable.getName()))).build());
					output.add(action);
				}
			}
		}
		return output;
	}

	@Override
	public Set<UserDefinedAction> collision(Collection<ActionInvoker> activations) {
		return new HashSet<>();
	}

	@Override
	public Set<UserDefinedAction> registerAction(UserDefinedAction action) {
		if (action.getActivation().getVariables().isEmpty()) {
			return new HashSet<>();
		}

		for (UserDefinedAction existing : registeredActions) {
			if (existing.equals(action)) {
				return new HashSet<>();
			}
		}
		registeredActions.add(action);
		return new HashSet<>();
	}

	@Override
	public Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
		Set<UserDefinedAction> output = new HashSet<>();
		for (Iterator<UserDefinedAction> iterator = registeredActions.iterator(); iterator.hasNext();) {
			UserDefinedAction existing = iterator.next();
			if (existing.equals(action)) {
				output.add(existing);
				iterator.remove();
			}
		}
		return output;
	}
}