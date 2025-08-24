package core.webui.webcommon;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;

public class HTTPLogger {
    private final String errorMessage;

    public HTTPLogger(String errorMsg) {
        if (errorMsg.isBlank()) throw new IllegalArgumentException("Error message must be a String!");
        errorMessage = errorMsg;
    }

    public final Void exec(RunnableVoid task, HttpAsyncExchange exchange) throws IOException {
        try {
            return task.run();
        } catch (NullPointerException e) {
            return HttpServerUtilities.prepareTextResponse(exchange, 404, getErrorMsg(e));
        } catch (IllegalArgumentException e) {
            return HttpServerUtilities.prepareTextResponse(exchange, 400, getErrorMsg(e));
        } catch (Exception e) {
            return HttpServerUtilities.prepareTextResponse(exchange, 500, getErrorMsg(e));
        }
    }

    private String getErrorMsg(Exception e) {
        return errorMessage + "\n" + e.getMessage();
    }
}