package core.ipc;

import java.io.IOException;
import java.io.OutputStream;

public final class IPCProtocol {

    private static final int MESSAGE_DELIMITER = 0x02;

    private IPCProtocol() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    /**
     * Send a message to the output stream.
     */
    public static void sendMessage(OutputStream writer, String message) throws IOException {
        writer.write(MESSAGE_DELIMITER);
        writer.write(MESSAGE_DELIMITER);
        writer.write(IPCCodec.encode(message));
        writer.write(MESSAGE_DELIMITER);
        writer.write(MESSAGE_DELIMITER);
        writer.flush();
    }
}