package com.github.jenya705.cubicauth.command;

import com.github.jenya705.cubicauth.*;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class RegisterCommand implements SimpleCommand {

    private final CubicAuth plugin;

    @Override
    @SneakyThrows
    public void execute(Invocation invocation) {
        if (!CommandUtils.validate(plugin, invocation)) return;
        Player player = (Player) invocation.source();
        String[] args = invocation.arguments();
        if (args.length != 2 || !args[0].equals(args[1])) {
            player.sendMessage(Component
                    .text("/reg <password> <password repeat>")
                    .color(NamedTextColor.RED));
            return;
        }
        UserLoginSession loginSession = plugin.getSession(player);
        if (loginSession.getUserModel() != null) {
            player.sendMessage(Component
                    .text(plugin.getConfig().getProperty(CubicAuthConfig.ALREADY_REGISTERED))
                    .color(NamedTextColor.RED)
            );
            return;
        }
        plugin.getDatabaseManager().upsertUser(
                UserModel.builder()
                        .username(player.getUsername())
                        .premium(false)
                        .hashPassword(PasswordEncryption.hashWithRandomSalt(args[0]))
                        .build()
        );
        player.sendMessage(Component
                .text(plugin.getConfig().getProperty(CubicAuthConfig.AUTHENTICATION_SUCCESS))
                .color(NamedTextColor.GREEN));
        plugin.authenticated(player);
    }

}
