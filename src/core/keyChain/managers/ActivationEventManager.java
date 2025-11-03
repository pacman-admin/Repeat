package core.keyChain.managers;

import core.config.Config;
import core.keyChain.ActionInvoker;
import core.keyChain.ActivationEvent;
import core.userDefinedTask.UserDefinedAction;

import java.util.Collection;
import java.util.List;
import java.util.Set;

abstract class ActivationEventManager {

    private Config config;

    ActivationEventManager(Config config) {
        this.config = config;
    }

    final Config getConfig() {
        return config;
    }

    public abstract void startListening();

    public abstract Set<UserDefinedAction> onActivationEvent(ActivationEvent event);

    public abstract void clear();

    protected abstract Set<UserDefinedAction> collision(Collection<ActionInvoker> activations);

    public final Set<UserDefinedAction> collision(ActionInvoker activation) {
        return collision(List.of(activation));
    }

    public abstract Set<UserDefinedAction> registerAction(UserDefinedAction action);

    public abstract Set<UserDefinedAction> unRegisterAction(UserDefinedAction action);
}
