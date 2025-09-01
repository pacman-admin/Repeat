package core.webui.server.handlers.renderedobjects;

import java.util.ArrayList;
import java.util.List;

import core.keyChain.SharedVariablesActivation;
import core.webui.server.handlers.internals.taskactivation.ActionTaskActivationAddSharedVariables;

public class RenderedSharedVariablesActivation {

	private List<RenderedSharedVariableActivation> variables;

	public static RenderedSharedVariablesActivation fromActivation(List<SharedVariablesActivation> variables) {
		RenderedSharedVariablesActivation output = new RenderedSharedVariablesActivation();
		output.variables = new ArrayList<>();
		for (SharedVariablesActivation variable : variables) {
			String namespace = variable.getVariable().isAll() ?  ActionTaskActivationAddSharedVariables.ALL : variable.getVariable().getNamespace();
			String name = variable.getVariable().isAllForNamespace() ?  ActionTaskActivationAddSharedVariables.ALL : variable.getVariable().getName();

			output.variables.add(RenderedSharedVariableActivation.of(namespace, name));
		}
		return output;
	}

	public List<RenderedSharedVariableActivation> getVariables() {
		return variables;
	}
	public void setVariables(List<RenderedSharedVariableActivation> variables) {
		this.variables = variables;
	}
}
