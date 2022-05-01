package com.github.jenya705.cubicauth.command;

import com.github.jenya705.cubicauth.CubicAuth;
import com.github.jenya705.cubicauth.CubicAuthConfig;
import com.github.jenya705.cubicauth.PasswordEncryption;
import com.github.jenya705.cubicauth.UserLoginSession;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class LoginCommand implements SimpleCommand {

    private final CubicAuth plugin;

    @Override
    public void execute(Invocation invocation) {
        if (!CommandUtils.validate(plugin, invocation)) return;
        Player player = (Player) invocation.source();
        String[] args = invocation.arguments();
        if (args.length != 1) {
            player.sendMessage(Component
                    .text("/login <password>")
                    .color(NamedTextColor.RED)
            );
            return;
        }
        UserLoginSession loginSession = plugin.getSession(player);
        if (!PasswordEncryption.isPasswordsEqual(args[0], loginSession.getUserModel().getHashPassword())) {
            player.sendMessage(Component
                    .text(plugin.getConfig().getProperty(CubicAuthConfig.BAD_PASSWORD))
                    .color(NamedTextColor.RED)
            );
            return;
        }
        player.sendMessage(Component
                .text(plugin.getConfig().getProperty(CubicAuthConfig.AUTHENTICATION_SUCCESS))
                .color(NamedTextColor.GREEN)
        );
        plugin.authenticated(player);
    }
}
