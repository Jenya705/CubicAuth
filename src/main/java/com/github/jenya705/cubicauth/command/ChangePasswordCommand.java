package com.github.jenya705.cubicauth.command;

import com.github.jenya705.cubicauth.CubicAuth;
import com.github.jenya705.cubicauth.CubicAuthConfig;
import com.github.jenya705.cubicauth.PasswordEncryption;
import com.github.jenya705.cubicauth.UserModel;
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
public class ChangePasswordCommand implements SimpleCommand {

    private final CubicAuth plugin;

    @Override
    @SneakyThrows
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(Component.text("Only for players"));
            return;
        }
        String[] args = invocation.arguments();
        if (args.length != 2) {
            player.sendMessage(Component
                    .text(plugin.getConfig().getProperty(CubicAuthConfig.CHANGE_PASSWORD))
                    .color(NamedTextColor.RED)
            );
            return;
        }
        UserModel userModel = plugin.getDatabaseManager()
                .getUser(player.getUsername())
                .orElseThrow();
        if (!PasswordEncryption.isPasswordsEqual(args[0], userModel.getHashPassword())) {
            player.sendMessage(Component
                    .text(plugin.getConfig().getProperty(CubicAuthConfig.BAD_PASSWORD))
                    .color(NamedTextColor.RED)
            );
            return;
        }
        plugin.getDatabaseManager()
                .upsertUser(UserModel.builder()
                        .username(player.getUsername())
                        .premium(userModel.isPremium())
                        .hashPassword(PasswordEncryption.hashWithRandomSalt(args[1]))
                        .build()
                );
        player.sendMessage(Component
                .text(plugin.getConfig().getProperty(CubicAuthConfig.SUCCESS))
                .color(NamedTextColor.GREEN)
        );
    }
}
