package core.controller;

import core.config.AbstractRemoteRepeatsClientsConfig;
import core.config.Config;
import core.controller.internals.*;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClient;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CoreProvider {

    private final Config config;
    private final RepeatsPeerServiceClientManager manager;

    public CoreProvider(Config config, RepeatsPeerServiceClientManager manager) {
        this.config = config;
        this.manager = manager;
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
                continue;
            }

            RepeatsPeerServiceClient client = manager.getClient(id);
            if (client == null) {
                continue;
            }

            mice.add(new RemoteRepeatsMouseCore(client.api()));
            keyboards.add(new RemoteRepeatsKeyboardCore(client.api()));
        }

        MouseCore mouse = new MouseCore(AggregateMouseCore.of(mice));
        KeyboardCore keyboard = new KeyboardCore(AggregateKeyboardCore.of(keyboards));
        return Core.getInstance(mouse, keyboard);
    }
}
