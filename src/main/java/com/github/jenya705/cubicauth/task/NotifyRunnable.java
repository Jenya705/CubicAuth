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
public class NotifyRunnable implements Runnable {

    private final CubicAuth plugin;
    private final Player player;
    private final boolean login;

    @Override
    public void run() {
        player.sendMessage(Component
                .text(login ?
                        plugin.getConfig().getProperty(CubicAuthConfig.LOGIN_NOTIFY) :
                        plugin.getConfig().getProperty(CubicAuthConfig.REGISTER_NOTIFY)
                )
                .color(NamedTextColor.GREEN)
        );
    }
}
