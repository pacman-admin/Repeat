/**
 * Copyright 2025 Langdon Staab
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
package core.controller;

import core.config.AbstractRemoteRepeatsClientsConfig;
import core.config.Config;
import core.controller.internals.AbstractKeyboardCoreImplementation;
import core.controller.internals.AbstractMouseCoreImplementation;
import core.controller.internals.AggregateKeyboardCore;
import core.controller.internals.AggregateMouseCore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public class CoreProvider {

    private final Config config;

    public CoreProvider(Config config) {
        this.config = config;
    }

    public Core getLocal() {
        return Core.local(config);
    }

    public Core get() {
        Set<String> clientIds = new HashSet<>(config.getCoreConfig().getClients());
        List<AbstractMouseCoreImplementation> mice = new ArrayList<>();
        List<AbstractKeyboardCoreImplementation> keyboards = new ArrayList<>();

        for (String id : clientIds) {
            if (id.equals(AbstractRemoteRepeatsClientsConfig.LOCAL_CLIENT)) {
                Core local = Core.local(config);
                mice.add(local.mouse());
                keyboards.add(local.keyBoard());
            }
        }

        MouseCore mouse = new MouseCore(AggregateMouseCore.of(mice));
        KeyboardCore keyboard = new KeyboardCore(AggregateKeyboardCore.of(keyboards));
        return Core.getInstance(mouse, keyboard);
    }
}
