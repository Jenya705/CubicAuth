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
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class JoinHandler {

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
    public void login(PreLoginEvent event) throws SQLException {
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

}
