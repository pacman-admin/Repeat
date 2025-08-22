package core.ipc.repeatServer;

import core.ipc.IPCServiceWithModifablePort;

import java.io.IOException;
import java.util.logging.Logger;

public class ControllerServer extends IPCServiceWithModifablePort {

    public static final int DEFAULT_TIMEOUT_MS = 10000;

    @Override
    protected void start() {
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
}