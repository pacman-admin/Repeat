package core.webui.server.handlers.internals;

import java.awt.Point;
import java.io.IOException;

import core.webui.server.handlers.AbstractGETHandler;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class GetMousePositionHandler extends AbstractGETHandler {

	public GetMousePositionHandler() {
		super("Could not get mouse pos!");
	}

	@Override
	protected String handle() {
		Point p = backEndHolder.getCoreProvider().getLocal().mouse().getPosition();
		return p.x + ", " + p.y;
	}
}
