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

import core.config.Config;
import core.keyChain.ActivationEvent;
import core.keyChain.ActivationEvent.EventType;
import core.keyChain.ButtonStroke;
import core.userDefinedTask.UserDefinedAction;

import java.util.HashSet;
import java.util.Set;

public abstract class KeyStrokeManager implements ActivationEventManager {
    private final Config config;

    KeyStrokeManager(Config config) {
        this.config = config;
    }

    final Config getConfig() {
        return config;
    }

    @Override
    public final Set<UserDefinedAction> onActivationEvent(ActivationEvent event) {
        if (event.getType() != EventType.BUTTON_STROKE) {
            return new HashSet<>();
        }

        ButtonStroke buttonStroke = event.getButtonStroke();
        if (buttonStroke.isPressed()) {
            return onButtonStrokePressed(buttonStroke);
        }
        return onButtonStrokeReleased(buttonStroke);
    }

    @Override
    public void stopListening() {
        //Nothing to do.
    }

    protected abstract Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke);

    protected abstract Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke);
}
