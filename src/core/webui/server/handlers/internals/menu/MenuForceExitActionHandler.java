package core.webui.server.handlers.internals.menu;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;



import java.util.Timer;
import java.util.TimerTask;

public final class MenuForceExitActionHandler extends AbstractSingleMethodHttpHandler {

    private static final long EXIT_DELAY_MS = 5;

    public MenuForceExitActionHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(1); // No clean up since user intentionally wants this.
            }
        }, EXIT_DELAY_MS);

        HttpServerUtilities.prepareHttpResponse(exchange, 200, "Exiting after " + EXIT_DELAY_MS + "ms..."); 
return;
    }
}
