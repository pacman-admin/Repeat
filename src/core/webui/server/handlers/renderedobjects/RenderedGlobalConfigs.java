package core.webui.server.handlers.renderedobjects;

public class RenderedGlobalConfigs {
    private RenderedRemoteRepeatsClientsConfig toolsConfigs;
    private RenderedRemoteRepeatsClientsConfig coreConfigs;
    private RenderedRemoteRepeatsClientsConfig remoteRepeatsCompilerConfigs;

    private RenderedGlobalConfigs() {
    }

    public static RenderedGlobalConfigs of(RenderedRemoteRepeatsClientsConfig toolsConfigs, RenderedRemoteRepeatsClientsConfig coreConfigs, RenderedRemoteRepeatsClientsConfig remoteRepeatsCompilerConfigs) {
        RenderedGlobalConfigs output = new RenderedGlobalConfigs();
        output.toolsConfigs = toolsConfigs;
        output.coreConfigs = coreConfigs;
        output.remoteRepeatsCompilerConfigs = remoteRepeatsCompilerConfigs;
        return output;
    }

    public RenderedRemoteRepeatsClientsConfig getToolsConfigs() {
        return toolsConfigs;
    }

    public RenderedRemoteRepeatsClientsConfig getCoreConfigs() {
        return coreConfigs;
    }

    public RenderedRemoteRepeatsClientsConfig getRemoteRepeatsCompilerConfigs() {
        return remoteRepeatsCompilerConfigs;
    }
}