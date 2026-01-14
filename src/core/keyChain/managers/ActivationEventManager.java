package core.keyChain.managers;

import core.keyChain.ActionInvoker;
import core.keyChain.ActivationEvent;
import core.userDefinedTask.UserDefinedAction;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ActivationEventManager {
    void startListening();

    void stopListening();

    Set<UserDefinedAction> onActivationEvent(ActivationEvent event);

    void clear();

    Set<UserDefinedAction> collision(Collection<ActionInvoker> activations);

    default Set<UserDefinedAction> collision(ActionInvoker activation) {
        return collision(List.of(activation));
    }

    Set<UserDefinedAction> registerAction(UserDefinedAction action);

    Set<UserDefinedAction> unRegisterAction(UserDefinedAction action);
}
