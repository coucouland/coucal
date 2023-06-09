package org.coucal.command;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.net.URI;

@CommandLine.Command(name = "server", description = "Start server for receiving requests",
        subcommands = {CommandLine.HelpCommand.class})
public class ServerCommand implements Runnable{

    private final String[] resourcePackages;

    private final String serverUri;

    public ServerCommand() {
        this("http://localhost:8000",
                "org.coucal.api", "org.coucal.api.controller");
    }

    public ServerCommand(String serverUri, String...resourcePackages) {
        this.serverUri = serverUri;
        this.resourcePackages = resourcePackages;
    }

    @Override
    public void run() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages(resourcePackages);

        Server server = JettyHttpContainerFactory.createServer(URI.create(serverUri), resourceConfig);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Shutting down the application...");
                server.stop();
                System.out.println("Done, exit.");
            } catch (Exception e) {
                LoggerFactory.getLogger(ServerCommand.class.getName()).error("Unexpected error", e);
            }
        }));

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
