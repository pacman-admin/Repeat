package core.userDefinedTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared variables used to pass values between tasks. This only supports string values since the tasks can be
 * written in different programming languages.
 */
@SuppressWarnings("unused")
public final class SharedVariables {

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
     */
    public static synchronized void set(String name, String data) {
        variables.put(name, data);
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
}
