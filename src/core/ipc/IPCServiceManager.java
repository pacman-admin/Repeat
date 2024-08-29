package core.ipc;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.cli.server.CliServer;
import core.ipc.repeatClient.CSharpIPCClientService;
import core.ipc.repeatClient.PythonIPCClientService;
import core.ipc.repeatServer.ControllerServer;
import core.languageHandler.Language;
import core.webui.server.UIServer;
import frontEnd.MainBackEndHolder;
import utilities.Function;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class IPCServiceManager {

    public static final int IPC_SERVICE_COUNT = IPCServiceName.values().length;
    private static final Logger LOGGER = Logger.getLogger(IPCServiceManager.class.getName());
    private static final long INTER_SERVICE_BOOT_TIME_MS = 200;
    private static final IIPCService[] ipcServices;
    private static final Map<Language, Integer> ipcByLanguage;

    static {
        ipcServices = new IIPCService[IPC_SERVICE_COUNT];
        ipcServices[IPCServiceName.CONTROLLER_SERVER.value()] = new ControllerServer();
        ipcServices[IPCServiceName.CLI_SERVER.value()] = new CliServer();
        ipcServices[IPCServiceName.WEB_UI_SERVER.value()] = new UIServer();
        ipcServices[IPCServiceName.PYTHON.value()] = new PythonIPCClientService();
        ipcServices[IPCServiceName.CSHARP.value()] = new CSharpIPCClientService();

        ipcByLanguage = new HashMap<>();
        ipcByLanguage.put(Language.JAVA, -1);
        ipcByLanguage.put(Language.PYTHON, IPCServiceName.PYTHON.value());
        ipcByLanguage.put(Language.CSHARP, IPCServiceName.CSHARP.value());
        ipcByLanguage.put(Language.MANUAL_BUILD, -1);
    }

    private IPCServiceManager() {
    }

    public static void setBackEnd(MainBackEndHolder backEnd) {
        ControllerServer controllerServer = (ControllerServer) ipcServices[IPCServiceName.CONTROLLER_SERVER.value()];
        controllerServer.setBackEnd(backEnd);
    }

    public static IIPCService getIPCService(Language name) {
        int index = ipcByLanguage.get(name);
        return index >= 0 ? ipcServices[index] : null;
    }

    public static IIPCService getIPCService(IPCServiceName name) {
        return name == null ? null : ipcServices[name.value()];
    }

    public static IIPCService getIPCService(int index) {
        return index >= IPC_SERVICE_COUNT ? null : ipcServices[index];

    }

    public static void initiateServices(MainBackEndHolder backEndHolder) throws IOException {
        for (IPCServiceName name : IPCServiceName.values()) {
            IIPCService service = IPCServiceManager.getIPCService(name);
            if (name == IPCServiceName.CLI_SERVER) {
                CliServer server = (CliServer) service;
                server.setMainBackEndHolder(backEndHolder);
            } else if (name == IPCServiceName.WEB_UI_SERVER) {
                UIServer server = (UIServer) service;
                server.setMainBackEndHolder(backEndHolder);
            }
            if (!service.isLaunchAtStartup()) {
                continue;
            }
            LOGGER.info("Starting ipc service " + service.getName());
            service.startRunning();

            try {
                Thread.sleep(INTER_SERVICE_BOOT_TIME_MS);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void stopServices() throws IOException {
        for (int i = IPCServiceName.values().length - 1; i >= 0; i--) {
            IPCServiceName name = IPCServiceName.values()[i];
            IIPCService service = IPCServiceManager.getIPCService(name);
            do {
                service.stopRunning();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (service.isRunning());
        }
    }

    public static boolean parseJSON(List<JsonNode> ipcSettings) {
        boolean result = true;

        for (JsonNode language : ipcSettings) {
            String name = language.getStringValue("name");
            IIPCService service = IPCServiceManager.getIPCService(IPCServiceName.identifyService(name));

            if (service != null) {
                boolean newResult = service.extractSpecificConfig(language.getNode("config"));
                if (!newResult) {
                    LOGGER.warning("Unable to parse config for ipc service " + name);
                }
                result &= newResult;
            }
        }

        return result;
    }

    public static JsonNode jsonize() {
        return JsonNodeFactories.array(new Function<IPCServiceName, JsonNode>() {
            @Override
            public JsonNode apply(IPCServiceName n) {
                IIPCService service = getIPCService(n);

                return JsonNodeFactories.object(JsonNodeFactories.field("name", JsonNodeFactories.string(n.toString())), JsonNodeFactories.field("config", service == null ? JsonNodeFactories.nullNode() : service.getSpecificConfig()));
            }
        }.map(IPCServiceName.values()));
    }
}
