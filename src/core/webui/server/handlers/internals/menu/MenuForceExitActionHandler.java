package core.webui.server.handlers.internals.menu;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.util.Timer;
import java.util.TimerTask;

public class MenuForceExitActionHandler extends AbstractSingleMethodHttpHandler {

    private static final long EXIT_DELAY_MS = 5;

    public MenuForceExitActionHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(1); // No clean up since user intentionally wants this.
            }
        }, EXIT_DELAY_MS);

        return HttpServerUtilities.prepareHttpResponse(exchange, 200, "Exiting after " + EXIT_DELAY_MS + "ms...");
    }
}
