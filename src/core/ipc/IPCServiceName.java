package core.ipc;

public enum IPCServiceName {
    WEB_UI_SERVER(0, "web_ui_server");
    private final int index;
    private final String name;

    IPCServiceName(final int index, final String name) {
        this.index = index;
        this.name = name;
    }

    static IPCServiceName identifyService(String name) {
        for (IPCServiceName service : IPCServiceName.values()) {
            if (name.equals(service.name)) {
                return service;
            }
        }
        return null;
    }

    public int value() {
        return index;
    }

    @Override
    public String toString() {
        return name;
    }
}
