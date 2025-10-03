/**
 * Copyright 2025 Langdon Staab
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Langdon Staab
 */
package core.webui.webcommon;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;

public class HTTPLogger {
    private final String errorMessage;

    public HTTPLogger(String errorMsg) {
        if (errorMsg.isBlank()) throw new IllegalArgumentException("Error message must be a String.");
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