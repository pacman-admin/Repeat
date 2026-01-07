package core.userDefinedTask;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Shared variables used to pass values between tasks. This only supports string values since the tasks can be
 * written in different programming languages.
 */
public class SharedVariables {

    private static final Logger LOGGER = Logger.getLogger(SharedVariables.class.getName());

    private static final Map<String, String> variables = new HashMap<>();

    private SharedVariables() {
        //This class is uninstantiable
    }

    /**
     * Creates a value with the provided name, set to the provided initial value
     * If a variable with that name already exists, this function does nothing.
     *
     * @param name         The name of the variable to create
     * @param initialValue The value the new variable will be set to.
     *
     */
    public static synchronized void create(String name, String initialValue) {
        if (!variables.containsKey(name)) variables.put(name, initialValue);
    }

    /**
     * Retrieve a variable value given namespace and variable name.
     *
     * @param name name of the variable.
     * @return value of the variable
     */
    public static synchronized String get(String name) {
        return variables.get(name);
    }

    /**
     * Set the value for a variable in a namespace.
     *
     * @param name variable name.
     * @param data value of the variable.
     * @return the existing value of the variable, or null if the variable does not exist before.
     */
    public static synchronized String set(String name, String data) {
        return variables.put(name, data);
    }

    /**
     * Delete the value for a variable in a namespace.
     *
     * @param name variable name.
     * @return the existing value of the variable, or null if the variable does not exist before.
     */
    public static synchronized String del(String name) {
        return variables.remove(name);
    }

    /**
     * Wait for the next call to set value of a variable with a timeout in milliseconds.
     * This will wait until the variable value is set, or timeout occurs.
     *
     * @return the new value of the variable, or null if timeout.
     */
//	public static String await(String namespace, String variable, long timeoutMs) {
//		Semaphore s = new Semaphore(0);
//		SharedVariablesPubSubManager.get().addSubscriber(SharedVariablesSubscriber.of(SharedVariablesSubscription.forVar(namespace, variable), e -> s.release()));
//		try {
//			if (!s.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
//				return null;
//			}
//		} catch (InterruptedException e) {
//			LOGGER.warning("Interrupted while waiting for semaphore.");
//			return null;
//		}
//
//		return getVar(namespace, variable);
//	}
}
