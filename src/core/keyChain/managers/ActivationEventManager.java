/**
 * Copyright 2026 Langdon Staab and HP Truong
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Langdon Staab
 * @author HP Truong
 */
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