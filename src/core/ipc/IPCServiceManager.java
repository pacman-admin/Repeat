package core.ipc;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.webui.server.UIServer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public final class IPCServiceManager {
    private static final UIServer UI_SERVER = new UIServer();
    private static final Logger LOGGER = Logger.getLogger(IPCServiceManager.class.getName());

    private IPCServiceManager() {
        //This class is uninstantiable
    }

    public static void stopServices(){
        UI_SERVER.stopRunning();
    }

    public static void parseJSON(List<JsonNode> ipcSettings) {
        for (JsonNode node : ipcSettings) {
            if (node.getStringValue("name").equals("web_ui_server")) {
                if (!UI_SERVER.extractSpecificConfig(node.getNode("config"))) {
                    LOGGER.warning("Could not parse IPC config");
                }
                return;
            }
        }
    }

    public static JsonNode jsonize() {
        return JsonNodeFactories.array(JsonNodeFactories.object(JsonNodeFactories.field("name", JsonNodeFactories.string("web_ui_server")), JsonNodeFactories.field("config", UI_SERVER.getSpecificConfig())));
    }

    public static void initiateServices() throws IOException {
        UI_SERVER.start();
    }

    public static IIPCService getUIServer() {
        return UI_SERVER;
    }
}