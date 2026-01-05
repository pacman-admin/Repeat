package core.ipc.repeatServer.processors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import core.ipc.ApiProtocol;
import core.ipc.repeatServer.MainMessageSender;
import utilities.ILoggable;

abstract class AbstractMessageProcessor implements ILoggable {

	final MainMessageSender messageSender;

	AbstractMessageProcessor(MainMessageSender messageSender) {
		this.messageSender = messageSender;
	}

	public abstract boolean process(String type, long id, JsonNode content);
	protected abstract boolean verifyMessageContent(JsonNode content);

	boolean verifyReplyContent(JsonNode content) {
		return content.isStringValue("status") &&
				content.isNode("message") &&
				content.isBooleanValue("is_reply_message") &&
				content.getBooleanValue("is_reply_message");
	}

	private boolean success(String type, long id, String message) {
		return messageSender.sendMessage(type, id, ApiProtocol.successReply(message));
	}

	boolean success(String type, long id, JsonNode message) {
		return messageSender.sendMessage(type, id, ApiProtocol.successReply( message));
	}

	boolean success(String type, long id) {
		return success(type, id, "");
	}

	boolean failure(String type, long id, String message) {
		getLogger().warning(message);
		StringWriter sw = new StringWriter();
		new Throwable("").printStackTrace(new PrintWriter(sw));
		getLogger().info(sw.toString());
		messageSender.sendMessage(type, id, ApiProtocol.failureReply(message));
		return false;
	}

	@Override
	public final Logger getLogger() {
		return Logger.getLogger(getClass().getName());
	}
}
