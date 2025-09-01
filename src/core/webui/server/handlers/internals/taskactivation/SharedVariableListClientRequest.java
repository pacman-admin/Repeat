package core.webui.server.handlers.internals.taskactivation;

import utilities.json.AutoJsonable;

import java.util.List;

class SharedVariableListClientRequest extends AutoJsonable {
    private List<SharedVariableClientRequest> vars;

    public List<SharedVariableClientRequest> getVars() {
        return vars;
    }
}