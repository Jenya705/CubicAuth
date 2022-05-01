package com.github.jenya705.cubicauth;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import com.github.jenya705.cubicauth.command.LoginCommand;
import com.github.jenya705.cubicauth.command.PremiumCommand;
import com.github.jenya705.cubicauth.command.RegisterCommand;
import com.github.jenya705.cubicauth.handler.DisconnectHandler;
import com.github.jenya705.cubicauth.handler.JoinHandler;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Plugin(
        id = "cubicauth",
        name = "CubicAuth",
        version = BuildConstants.VERSION,
        description = "Auth plugin for Cubic",
        authors = {"Jenya705"}
)
@Getter
public class CubicAuth {

    private final Logger logger;
    private final ProxyServer server;
    private final SettingsManager config;
    private final DatabaseManager databaseManager;

    private final Map<String, UserLoginSession> loginSessions = new ConcurrentHashMap<>();

    private boolean disabled = false;

    @Inject
    public CubicAuth(Logger logger, ProxyServer server, @DataDirectory Path dataDirectory) throws IOException, ClassNotFoundException {
        this.logger = logger;
        this.server = server;
        dataDirectory.toFile().mkdirs();
        dataDirectory.resolve("config.yml").toFile().createNewFile();
        this.config = SettingsManagerBuilder
                .withYamlFile(dataDirectory.resolve("config.yml"))
                .configurationData(CubicAuthConfig.class)
                .create();
        this.databaseManager = new DatabaseManager(this);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws SQLException {
        if (server.getServer(config.getProperty(CubicAuthConfig.LIMBO_SERVER)).isEmpty()) {
            logger.error("Server with given name is not exist");
            disabled = true;
            return;
        }
        databaseManager.reload();
        databaseManager.setup();
        server.getEventManager().register(this, new JoinHandler(this));
        server.getEventManager().register(this, new DisconnectHandler(this));
        server.getCommandManager().register("register", new RegisterCommand(this), "reg");
        server.getCommandManager().register("login", new LoginCommand(this), "log", "l");
        server.getCommandManager().register("premium", new PremiumCommand(this), "p");
    }

    public void authenticated(Player player) {
        UserLoginSession session = loginSessions.remove(player.getUsername());
        if (session == null) return;
        player.createConnectionRequest(session.getThenConnect())
                .connect()
                .thenAccept(result -> result.getReasonComponent().ifPresent(player::disconnect));
        session.getTasks().forEach(ScheduledTask::cancel);
    }

    public void forceRemoveSession(Player player) {
        UserLoginSession session = loginSessions.remove(player.getUsername());
        if (session == null) return;
        session.getTasks().forEach(ScheduledTask::cancel);
    }

    public UserLoginSession getSession(Player player) {
        return loginSessions.get(player.getUsername());
    }

}
