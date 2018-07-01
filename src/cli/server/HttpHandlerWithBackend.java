package cli.server;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import frontEnd.MainBackEndHolder;

public abstract class HttpHandlerWithBackend implements HttpHandler {
	protected MainBackEndHolder backEndHolder;

	public synchronized void setMainBackEndHolder(MainBackEndHolder backEndHolder) {
		this.backEndHolder = backEndHolder;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if (backEndHolder == null) {
			Codec.prepareResponse(exchange, 500, "Missing backend...");
			return;
		}
		handleWithBackend(exchange);
	}

	protected abstract void handleWithBackend(HttpExchange exchange) throws IOException;
}
