package core.webui.server.handlers.renderedobjects;

import java.util.List;

public class RenderedSharedVariablesActivation {

    private final List<RenderedSharedVariableActivation> variables = List.of();

    public static RenderedSharedVariablesActivation of() {
        return new RenderedSharedVariablesActivation();
    }

    public List<RenderedSharedVariableActivation> getVariables() {
        return variables;
    }
}
