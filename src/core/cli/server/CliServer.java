/**
 * Copyright 2025 Langdon Staab
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Langdon Staab
 * @author HP Truong
 */
package core.cli.server;

import core.cli.server.handlers.*;
import core.config.CliConfig;
import core.ipc.IPCServiceWithModifablePort;
import core.webui.webcommon.HttpHandlerWithBackend;
import core.webui.webcommon.UpAndRunningHandler;
import frontEnd.MainBackEndHolder;
import org.apache.http.ExceptionLogger;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CliServer extends IPCServiceWithModifablePort {

    private static final int TERMINATION_DELAY_SECOND = 1;
    private static final int DEFAULT_PORT = CliConfig.DEFAULT_SERVER_PORT;

    private Map<String, HttpHandlerWithBackend> handlers;

    private MainBackEndHolder backEndHolder;
    private Thread mainThread;
    private HttpServer server;

    public CliServer() {
        setPort(DEFAULT_PORT);
    }

    public synchronized void setMainBackEndHolder(MainBackEndHolder backEndHolder) {
        this.backEndHolder = backEndHolder;
        if (handlers == null) {
            return;
        }

        for (HttpHandlerWithBackend handler : handlers.values()) {
            handler.setMainBackEndHolder(backEndHolder);
        }
    }

    private Map<String, HttpHandlerWithBackend> createHandlers() {
        Map<String, HttpHandlerWithBackend> output = new HashMap<>();
        output.put("/var/get", new SharedVariablesGetActionHandler());
        output.put("/var/set", new SharedVariablesSetActionHandler());

        output.put("/task/add", new TaskAddActionHandler());
        output.put("/task/remove", new TaskRemoveActionHandler());
        output.put("/task/execute", new TaskExecuteActionHandler());
        output.put("/task/list", new TaskListActionHandler());
        return output;
    }

    @Override
    protected void start() throws IOException {
        handlers = createHandlers();
        setMainBackEndHolder(backEndHolder);

        ServerBootstrap serverBootstrap = ServerBootstrap.bootstrap().setLocalAddress(InetAddress.getByName("localhost")).setListenerPort(port).setServerInfo("RepeatCli").setExceptionLogger(ExceptionLogger.STD_ERR).registerHandler("/test", new UpAndRunningHandler());
        for (Entry<String, HttpHandlerWithBackend> entry : handlers.entrySet()) {
            serverBootstrap.registerHandler(entry.getKey(), entry.getValue());
        }
        server = serverBootstrap.create();

        mainThread = new Thread(() -> {
            try {
                server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                getLogger().log(Level.SEVERE, "Interrupted when waiting for CLI server.", e);
            }
            getLogger().info("Finished waiting for CLI server termination...");
        });
        server.start();
        mainThread.start();
        getLogger().fine("CLI server up and running...");
    }

    @Override
    protected void stop() throws IOException {
        server.shutdown(TERMINATION_DELAY_SECOND, TimeUnit.SECONDS);
        try {
            mainThread.join();
            getLogger().info("CLI server terminated!");
        } catch (InterruptedException e) {
            getLogger().log(Level.WARNING, "Interrupted when waiting for server to terminate.", e);
        }
        server = null;
        mainThread = null;
        handlers.clear();
    }

    @Override
    public boolean isRunning() {
        return mainThread != null && server != null;
    }

    @Override
    public String getName() {
        return "CLI server";
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(CliServer.class.getName());
    }
}
