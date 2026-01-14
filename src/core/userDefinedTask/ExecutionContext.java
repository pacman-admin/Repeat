package core.userDefinedTask;

import core.controller.Core;
import core.keyChain.ActionInvoker;

public final class ExecutionContext {
    private Core controller;
    private ActionInvoker activation;

    private ExecutionContext() {
    }

    public ActionInvoker getActivation() {
        return activation;
    }

    public Core getController() {
        return controller;
    }

    public static final class Builder {
        private Core controller;
        private ActionInvoker activation;

        public static Builder of() {
            return new Builder();
        }

        public Builder setController(Core controller) {
            this.controller = controller;
            return this;
        }

        public Builder setActivation(ActionInvoker activation) {
            this.activation = activation;
            return this;
        }

        public ExecutionContext build() {
            ExecutionContext result = new ExecutionContext();
            result.controller = controller;
            result.activation = activation;
            return result;
        }
    }
}
