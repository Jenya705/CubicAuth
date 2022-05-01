package com.github.jenya705.cubicauth.command;

import com.github.jenya705.cubicauth.CubicAuth;
import com.github.jenya705.cubicauth.CubicAuthConfig;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * @author Jenya705
 */
@UtilityClass
public class CommandUtils {

    public boolean validate(CubicAuth plugin, SimpleCommand.Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(Component.text("Only for players!"));
            return false;
        }
        if (player.getCurrentServer()
                .filter(it -> it.getServer().getServerInfo().getName().equals(
                        plugin.getConfig().getProperty(CubicAuthConfig.LIMBO_SERVER)
                ))
                .isEmpty()
        ) {
            player.sendMessage(Component
                    .text(plugin.getConfig().getProperty(CubicAuthConfig.ALREADY_AUTHENTICATED))
                    .color(NamedTextColor.RED)
            );
            return false;
        }
        return true;
    }

}
