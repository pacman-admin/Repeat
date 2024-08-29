package core.ipc.repeatClient.repeatPeerClient.api;

import utilities.json.AutoJsonable;

import java.util.List;

@SuppressWarnings("unused")
public abstract class DeviceCommand extends AutoJsonable {
    @SuppressWarnings("FieldCanBeLocal")
    public static final class IntDeviceCommand extends DeviceCommand {
        private final String device;
        private final String action;
        private final List<Integer> parameters;

        IntDeviceCommand(String device, String action, List<Integer> parameters) {
            this.device = device;
            this.action = action;
            this.parameters = parameters;
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    static final class StringDeviceCommand extends DeviceCommand {
        private final String device;
        private final String action;
        private final List<String> parameters;

        StringDeviceCommand(String device, String action, List<String> parameters) {
            this.device = device;
            this.action = action;
            this.parameters = parameters;
        }
    }
}
