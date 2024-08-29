package core.ipc.repeatClient.repeatPeerClient.api;

import argo.jdom.JsonNode;
import core.ipc.ApiProtocol;
import core.ipc.repeatClient.repeatPeerClient.RepeatPeerServiceClientWriter;
import core.ipc.repeatClient.repeatPeerClient.ResponseManager.Reply;
import core.ipc.repeatServer.processors.IpcMessageType;
import utilities.json.IJsonable;

import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractRepeatsClientApi {

    private static final Logger LOGGER = Logger.getLogger(AbstractRepeatsClientApi.class.getName());

    private final RepeatPeerServiceClientWriter repeatPeerServiceClientWriter;

    AbstractRepeatsClientApi(RepeatPeerServiceClientWriter repeatPeerServiceClientWriter) {
        this.repeatPeerServiceClientWriter = repeatPeerServiceClientWriter;
    }

    final String waitAndGetResponseIfSuccess(IpcMessageType type, IJsonable message) {
        JsonNode node = waitAndGetJsonResponseIfSuccess(type, message);
        return node == null ? "" : node.getStringValue();
    }

    final JsonNode waitAndGetJsonResponseIfSuccess(IpcMessageType type, IJsonable message) {
        long id = repeatPeerServiceClientWriter.enqueueMessage(type, message);
        Reply reply;
        try {
            reply = repeatPeerServiceClientWriter.waitForReply(id);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted when running command", e);
            return null;
        }

        return reply.getStatus().equals(ApiProtocol.SUCCESS_STATUS) ? reply.getMessage() : null;
    }
}
