package core.ipc;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.webui.server.UIServer;
import frontEnd.MainBackEndHolder;

import java.io.IOException;
import java.util.List;

public final class IPCServiceManager {
    private static final IIPCService UI_SERVER;

    static {
        UI_SERVER = new UIServer();
    }
    private IPCServiceManager() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    public static void stopServices() throws IOException {
        UI_SERVER.stopRunning();
        while (UI_SERVER.isRunning()) ;
    }

    public static boolean parseJSON(List<JsonNode> ipcSettings) {
        for (JsonNode language : ipcSettings) {
            String name = language.getStringValue("name");
            if (name.equals("web_ui_server")) {
                return UI_SERVER.extractSpecificConfig(language.getNode("config"));
            }
        }
        throw new RuntimeException("UI Server not found.");
    }

    public static JsonNode jsonize() {
        return JsonNodeFactories.array(JsonNodeFactories.object(JsonNodeFactories.field("name", JsonNodeFactories.string("web_ui_server")), JsonNodeFactories.field("config", UI_SERVER == null ? JsonNodeFactories.nullNode() : UI_SERVER.getSpecificConfig())));
    }

    public static void initiateServices(MainBackEndHolder backEndHolder) throws IOException {
        UIServer server = (UIServer) UI_SERVER;
        server.setMainBackEndHolder(backEndHolder);
        UI_SERVER.startRunning();
    }

    public static IIPCService getUIServer(){
        return UI_SERVER;
    }
}