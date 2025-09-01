package core.ipc.repeatClient.repeatPeerClient.api;

import java.util.List;

import utilities.json.AutoJsonable;

@SuppressWarnings("unused")
abstract class DeviceCommand extends AutoJsonable {

	public static class IntDeviceCommand extends DeviceCommand {

        IntDeviceCommand(String device, String action, List<Integer> parameters) {
        }
	}

	static class StringDeviceCommand extends DeviceCommand {

        StringDeviceCommand(String device, String action, List<String> parameters) {
        }
	}
}
