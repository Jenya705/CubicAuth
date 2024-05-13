package com.github.jenya705.cubicauth.handler;

import com.github.jenya705.cubicauth.CubicAuth;
import com.github.jenya705.cubicauth.CubicAuthConfig;
import com.github.jenya705.cubicauth.UserLoginSession;
import com.github.jenya705.cubicauth.UserModel;
import com.github.jenya705.cubicauth.task.DisconnectRunnable;
import com.github.jenya705.cubicauth.task.NotifyRunnable;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class JoinHandler {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,16}");

    private final CubicAuth plugin;

    @Subscribe(order = PostOrder.LAST)
    public void join(PlayerChooseInitialServerEvent event) {
        UserLoginSession session = plugin.getSession(event.getPlayer());
        if (session == null) return;
        session.setThenConnect(event.getInitialServer().orElseThrow());
        session.setTasks(new ArrayList<>());
        session.getTasks().add(plugin.getServer().getScheduler()
                .buildTask(plugin, new NotifyRunnable(
                        plugin, event.getPlayer(), session.getUserModel() != null))
                .repeat(Duration.ofSeconds(
                        plugin.getConfig().getProperty(CubicAuthConfig.AUTH_MESSAGE_REPEAT)))
                .clearDelay()
                .schedule()
        );
        session.getTasks().add(plugin.getServer().getScheduler()
                .buildTask(plugin, new DisconnectRunnable(plugin, event.getPlayer()))
                .delay(Duration.ofSeconds(
                        plugin.getConfig().getProperty(CubicAuthConfig.AUTHENTICATION_TIME)))
                .clearRepeat()
                .schedule()
        );
        event.setInitialServer(plugin
                .getServer()
                .getServer(plugin.getConfig().getProperty(CubicAuthConfig.LIMBO_SERVER))
                .orElseThrow()
        );
    }

    @Subscribe
    public void connected(ServerConnectedEvent event) {
        if (!event.getServer().getServerInfo()
                .getName().equals(plugin.getConfig().getProperty(CubicAuthConfig.LIMBO_SERVER)) &&
                plugin.getSession(event.getPlayer()) != null) {
            event.getPlayer().disconnect(Component
                    .text(plugin.getConfig().getProperty(CubicAuthConfig.CONTACT_ADMINISTRATOR))
                    .color(NamedTextColor.BLUE)
            );
        }
    }

    @Subscribe
    public void login(PreLoginEvent event) throws SQLException {
        Matcher nicknameMatcher = NICKNAME_PATTERN.matcher(event.getUsername());
        if (!nicknameMatcher.find() || nicknameMatcher.start() != 0 || nicknameMatcher.end() != event.getUsername().length()) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component
                    .text(plugin.getConfig().getProperty(CubicAuthConfig.BAD_CHARACTERS))
                    .color(NamedTextColor.BLUE)
            ));
            return;
        }
        UserModel userModel = plugin
                .getDatabaseManager()
                .getUser(event.getUsername())
                .orElse(null);
        if (userModel == null || !userModel.isPremium()) {
            plugin.getLoginSessions().put(
                    event.getUsername(),
                    UserLoginSession
                            .builder()
                            .userModel(userModel)
                            .build()
            );
            return;
        }
        event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
    }

    @Subscribe
    public void profile(GameProfileRequestEvent event) throws SQLException {
        plugin
                .getDatabaseManager()
                .getUser(event.getUsername())
                .ifPresent(userModel -> event
                        .setGameProfile(event
                                .getGameProfile()
                                .withId(UUID.nameUUIDFromBytes(
                                        ("OfflinePlayer:" + userModel.getUsername()).getBytes(StandardCharsets.UTF_8)
                                ))
                        )
                );
    }

}
