package core.ipc;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import utilities.ILoggable;

import java.io.IOException;

public abstract class IIPCService implements ILoggable {

    protected int port;
    private boolean launchAtStartup;

    protected IIPCService() {
        launchAtStartup = true;
    }

    public final void startRunning() throws IOException {
        if (!isRunning()) {
            start();
        } else {
            getLogger().info("This service is already running.");
        }
    }

    public final void stopRunning() throws IOException {
        if (!isRunning()) {
            return;
        }

        stop();
    }

    /**
     * Specific configuration parameters for this ipc service.
     *
     * @return the json node containing configuration parameters for this ipc service.
     */
    protected JsonNode getSpecificConfig() {
        return JsonNodeFactories.object(JsonNodeFactories.field("launch_at_startup", JsonNodeFactories.booleanNode(launchAtStartup)));
    }

    /**
     * Extract internal configuration parameters for this ipc service.
     *
     * @param node the json node containing configuration parameters for this ipc service.
     * @return if parsing was successful.
     */
    protected boolean extractSpecificConfig(JsonNode node) {
        launchAtStartup = node.getBooleanValue("launch_at_startup");
        return true;
    }

    protected abstract void start() throws IOException;

    protected abstract void stop() throws IOException;

    public abstract boolean isRunning();

    public boolean setPort(int newPort) {
        if (isRunning()) {
            getLogger().warning("Cannot change port while running!");
            return false;
        }
        this.port = newPort;
        return true;
    }

    public final int getPort() {
        return port;
    }

    public abstract String getName();

    public final boolean isLaunchAtStartup() {
        return launchAtStartup;
    }

    public final void setLaunchAtStartup(boolean launchAtStartup) {
        this.launchAtStartup = launchAtStartup;
    }
}
