package core.webui.server.handlers.renderedobjects;

import core.ipc.IIPCService;

public final class RenderedIPCService {
	private String name;
	private String port;

	public static RenderedIPCService of(IIPCService service) {
		RenderedIPCService output = new RenderedIPCService();
		output.name = service.getName();
		output.port = service.getPort() + "";
		return output;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
