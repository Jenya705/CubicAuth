package com.github.jenya705.cubicauth.task;

import com.github.jenya705.cubicauth.CubicAuth;
import com.github.jenya705.cubicauth.CubicAuthConfig;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class DisconnectRunnable implements Runnable {

    private final CubicAuth plugin;
    private final Player player;

    @Override
    public void run() {
        player.disconnect(Component
                .text(plugin.getConfig().getProperty(CubicAuthConfig.AUTHENTICATION_TIME_EXCEEDED))
                .color(NamedTextColor.BLUE)
        );
    }
}
