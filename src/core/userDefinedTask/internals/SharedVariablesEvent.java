package core.userDefinedTask.internals;

public record SharedVariablesEvent(String namespace, String name) {
    public static SharedVariablesEvent of(String namespace, String name) {
        return new SharedVariablesEvent(namespace, name);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }
}