package core.ipc;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import utilities.json.JSONUtility;

/**
 * IPC service with modifiable port to start at. E.g. a server.
 */
public abstract class IPCServiceWithModifiablePort extends IIPCService {
	protected static boolean portUnavailable(int port) {
		try {
			ServerSocket socket = new ServerSocket(port);
			socket.close();
			return false;
		} catch (IOException e) {
			return true;
		}
	}

	@Override
	protected boolean extractSpecificConfig(JsonNode node) {
		boolean result = true;
		if (!super.extractSpecificConfig(node)) {
			getLogger().warning("Cannot parse parent config for " + IPCServiceWithModifiablePort.class);
			result = false;
		}

		// If port not specified then use default port.
		if (!node.isNumberValue("port")) {
			return result;
		}

		try {
			String portString = node.getNumberValue("port");
			int port = Integer.parseInt(portString);
			return result && setPort(port);
		} catch (NumberFormatException e) {
			getLogger().log(Level.WARNING, "Controller service port is not an integer.", e);
			return false;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Cannot parse controller config.", e);
			return false;
		}
	}

	@Override
	protected final JsonNode getSpecificConfig() {
		 return JSONUtility.addChild(super.getSpecificConfig(), "port", JsonNodeFactories.number(port));
	}
	@Override
	public boolean setPort(int newPort) {
		if (portUnavailable(newPort)) {
			return false;
		}
		this.port = newPort;
		return true;
	}
}
