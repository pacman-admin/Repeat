package core.ipc;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;

public final class ApiProtocol {
	private ApiProtocol() {
        throw new InstantiationError("This class is uninstantiable.");
    }

	public static final String SUCCESS_STATUS = "Success";
	private static final String FAILURE_STATUS = "Failure";

	public static JsonNode keepAliveMessage() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("device", JsonNodeFactories.string("system")),
				JsonNodeFactories.field("action", JsonNodeFactories.string("keep_alive")),
				JsonNodeFactories.field("parameters", JsonNodeFactories.array())
				);
	}

	public static JsonNode successReply(String message) {
		return generateReply(SUCCESS_STATUS, message);
	}

	public static JsonNode successReply(JsonNode message) {
		return generateReply(SUCCESS_STATUS, message);
	}

	public static JsonNode failureReply(String message) {
		return generateReply(FAILURE_STATUS, message);
	}

	public static boolean isReplyMessage(JsonNode message) {
		return message.isStringValue("status") &&
				message.isNode("message") &&
				message.isBooleanValue("is_reply_message") &&
				message.getBooleanValue("is_reply_message");
	}

	private static JsonNode generateReply(String status, String message) {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("status", JsonNodeFactories.string(status)),
				JsonNodeFactories.field("message", JsonNodeFactories.string(message)),
				JsonNodeFactories.field("is_reply_message", JsonNodeFactories.booleanNode(true))
				);
	}

	private static JsonNode generateReply(String status, JsonNode message) {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("status", JsonNodeFactories.string(status)),
				JsonNodeFactories.field("message", message),
				JsonNodeFactories.field("is_reply_message", JsonNodeFactories.booleanNode(true))
				);
	}
}
