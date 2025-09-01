package core.ipc.repeatServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.ipc.IPCProtocol;
import utilities.ILoggable;
import utilities.json.JSONUtility;

/**
 * This class sends messages to the ipc client
 * A generic message will have the following JSON format:
 * {
 * 		"id" : id of the message as integer,
 * 		"type" : type of the message as string. See {@link core.ipc.repeatServer.processors.ServerMainProcessor}
 * 		"content" : JSON content of the message (determined by upper layer)
 * }
 *
 * @author HP Truong
 *
 */
public class MainMessageSender implements ILoggable {

	private long idCount;
	private DataOutputStream writer;

	MainMessageSender() {
		idCount = 1L;
	}

	public synchronized long sendMessage(String type, JsonNode content) {
		long id = newID();
		if (sendMessage(type, id, content)) {
			return id;
		} else {
			return -1;
		}
	}

	public synchronized boolean sendMessage(String type, long id, JsonNode content) {
		JsonRootNode toSend = getMessage(type, id, content);

		synchronized (this) {
			try {
				IPCProtocol.sendMessage(writer, JSONUtility.jsonToString(toSend));
			} catch (IOException e) {
				getLogger().log(Level.WARNING, "Exception while writing message", e);
				return false;
			}
		}

		return true;
	}

	private JsonRootNode getMessage(String type, long id, JsonNode message) {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("type", JsonNodeFactories.string(type)),
				JsonNodeFactories.field("id", JsonNodeFactories.number(id)),
				JsonNodeFactories.field("content", message)
				);
	}

	private synchronized long newID() {
		idCount++;
		return idCount;
	}

	void setWriter(DataOutputStream writer) {
		this.writer = writer;
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(MainMessageSender.class.getName());
	}
}
