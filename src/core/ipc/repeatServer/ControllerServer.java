package core.ipc.repeatServer;

import core.ipc.IPCServiceWithModifablePort;
import frontEnd.MainBackEndHolder;

import java.io.IOException;
import java.util.logging.Logger;

public class ControllerServer extends IPCServiceWithModifablePort {

    public static final int DEFAULT_TIMEOUT_MS = 10000;

    @Override
    protected void start() throws IOException {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public String getName() {
        return "pretend Controller server";
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(ControllerServer.class.getName());
    }

    public void setBackEnd(MainBackEndHolder ignored) {
    }
}