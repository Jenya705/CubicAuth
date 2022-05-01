package com.github.jenya705.cubicauth.command;

import com.github.jenya705.cubicauth.CubicAuth;
import com.github.jenya705.cubicauth.CubicAuthConfig;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.permission.Tristate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class ForceUnregisterCommand implements SimpleCommand {

    private final CubicAuth plugin;

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().getPermissionValue("cubic.auth.forceunregister") == Tristate.TRUE;
    }

    @Override
    @SneakyThrows
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length != 1) {
            invocation.source().sendMessage(Component
                    .text("/forceunregister <username>")
                    .color(NamedTextColor.RED)
            );
            return;
        }
        plugin.getDatabaseManager().deleteUser(args[0]);
        plugin.getServer()
                .getPlayer(args[0])
                .ifPresent(p -> p.disconnect(Component
                        .text(plugin.getConfig().getProperty(CubicAuthConfig.NEED_REGISTER))
                        .color(NamedTextColor.BLUE)
                ));
        invocation.source().sendMessage(Component
                .text(plugin.getConfig().getProperty(CubicAuthConfig.SUCCESS))
                .color(NamedTextColor.GREEN)
        );
    }
}
