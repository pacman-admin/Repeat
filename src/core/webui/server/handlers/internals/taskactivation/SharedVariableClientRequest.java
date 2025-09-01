package core.webui.server.handlers.internals.taskactivation;

import utilities.json.AutoJsonable;

class SharedVariableClientRequest extends AutoJsonable {
    private String namespace;
    private String name;

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }
}