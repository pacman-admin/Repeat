package core.userDefinedTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import core.userDefinedTask.internals.SharedVariablesEvent;
import core.userDefinedTask.internals.SharedVariablesPubSubManager;
import core.userDefinedTask.internals.SharedVariablesSubscriber;
import core.userDefinedTask.internals.SharedVariablesSubscription;

/**
 * Shared variables used to pass values between tasks. This only supports string values since the tasks can be
 * written in different programming languages.
 */
public class SharedVariables {

	private static final Logger LOGGER = Logger.getLogger(SharedVariables.class.getName());

	public static final String GLOBAL_NAMESPACE = "global";
	private static final Map<String, Map<String, String>> variables = new HashMap<>();

	private final String namespace;

	private SharedVariables(String namespace) {
		this.namespace = namespace;
	}
	/**
	 * Retrieve a variable value given namespace and variable name.
	 *
	 * @param namespace namespace where this variable belongs.
	 * @param variable name of the variable.
	 * @return value of the variable
	 */
	public static synchronized String getVar(String namespace, String variable) {
		Map<String, String> namespaceVariables = variables.get(namespace);
		if (namespaceVariables == null) {
			return null;
		}

		return namespaceVariables.get(variable);
	}

	/**
	 * Set the value for a variable in a namespace.
	 *
	 * @param namespace namespace where the variable belongs.
	 * @param variable variable name.
	 * @param value value of the variable.
	 * @return the existing value of the variable, or null if the variable does not exist before.
	 */
	public static synchronized String setVar(String namespace, String variable, String value) {
		if (isNullValue(namespace, "namespace") || isNullValue(variable, "variable") || isNullValue(value, "value")) {
			return null;
		}

		if (!variables.containsKey(namespace)) {
			variables.put(namespace, new HashMap<>());
		}

		Map<String, String> namespaceVariables = variables.get(namespace);
		String output = namespaceVariables.put(variable, value);

		SharedVariablesPubSubManager.get().notifyEvent(SharedVariablesEvent.of(namespace, variable));
		return output;
	}

	/**
	 * Delete the value for a variable in a namespace.
	 *
	 * @param namespace namespace where the variable belongs.
	 * @param variable variable name.
	 * @return the existing value of the variable, or null if the variable does not exist before.
	 */
	public static synchronized String delVar(String namespace, String variable) {
		if (isNullValue(namespace, "namespace") || isNullValue(variable, "variable")) {
			return null;
		}

		if (!variables.containsKey(namespace)) {
			return null;
		}

		Map<String, String> namespaceVariables = variables.get(namespace);
		String result = namespaceVariables.remove(variable);

		if (namespaceVariables.isEmpty()) {
			variables.remove(namespace);
		}

		return result;
	}

	/**
	 * Wait for the next call to set value of a variable with a timeout in milliseconds.
	 * This will wait until the variable value is set, or timeout occurs.
	 *
	 * @return the new value of the variable, or null if timeout.
	 */
	public static String waitVar(String namespace, String variable, long timeoutMs) {
		Semaphore s = new Semaphore(0);
		SharedVariablesPubSubManager.get().addSubscriber(SharedVariablesSubscriber.of(SharedVariablesSubscription.forVar(namespace, variable), e -> s.release()));
		try {
			if (!s.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
				return null;
			}
		} catch (InterruptedException e) {
			LOGGER.warning("Interrupted while waiting for semaphore.");
			return null;
		}

		return getVar(namespace, variable);
	}

	/**
	 * Simply check if the value is null or not and log a warning message if it is null.
	 *f
	 * @param value the value to check.
	 * @param valueMeaning meaning of the value.
	 * @return if the value is null.
	 */
	private static boolean isNullValue(String value, String valueMeaning) {
		if (value == null) {
			LOGGER.warning("Setting null " + valueMeaning + ".");
			return true;
		}

		return false;
	}
}
