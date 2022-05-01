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
public class PremiumCommand implements SimpleCommand {

    private final CubicAuth plugin;

    @Override
    @SneakyThrows
    public void execute(Invocation invocation) {
        if (!CommandUtils.validate(plugin, invocation)) return;
        Player player = (Player) invocation.source();
        UserLoginSession session = plugin.getSession(player);
        UserModel userModel = session.getUserModel();
        if (userModel == null) {
            plugin.getDatabaseManager().upsertUser(UserModel.builder()
                    .username(player.getUsername())
                    .premium(true)
                    .hashPassword("")
                    .build()
            );
            plugin.authenticated(player);
            return;
        }
        String[] args = invocation.arguments();
        if (args.length != 1) {
            player.sendMessage(Component
                    .text("/premium <password>")
                    .color(NamedTextColor.RED)
            );
            return;
        }
        if (!PasswordEncryption.isPasswordsEqual(args[0], userModel.getHashPassword())) {
            player.sendMessage(Component
                    .text(plugin.getConfig().getProperty(CubicAuthConfig.BAD_PASSWORD))
                    .color(NamedTextColor.RED)
            );
            return;
        }
        plugin.getDatabaseManager().upsertUser(UserModel.builder()
                .username(userModel.getUsername())
                .premium(true)
                .hashPassword(userModel.getHashPassword())
                .build()
        );
        player.sendMessage(Component
                .text(plugin.getConfig().getProperty(CubicAuthConfig.AUTHENTICATION_SUCCESS))
                .color(NamedTextColor.GREEN)
        );
        plugin.authenticated(player);
    }
}
