package com.github.jenya705.cubicauth;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jenya705
 */
@Data
@SuperBuilder
public class UserLoginSession {

    private final UserModel userModel;
    private final AtomicInteger attempts = new AtomicInteger();

    private RegisteredServer thenConnect;
    private List<ScheduledTask> tasks;

    public void newAttempt(CubicAuth plugin, Player player) {
        if (attempts.incrementAndGet() >= plugin.getConfig().getProperty(CubicAuthConfig.MAXIMUM_ATTEMPTS)) {
            player.disconnect(Component
                    .text(plugin.getConfig().getProperty(CubicAuthConfig.MAXIMUM_ATTEMPTS_REASON))
                    .color(NamedTextColor.BLUE)
            );
        }
    }

}
