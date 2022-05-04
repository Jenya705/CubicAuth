package com.github.jenya705.cubicauth.command;

import com.github.jenya705.cubicauth.CubicAuth;
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
public class IsRegisteredCommand implements SimpleCommand {

    private final CubicAuth plugin;

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().getPermissionValue("cubic.auth.isregistered") == Tristate.TRUE;
    }

    @Override
    @SneakyThrows
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length != 1) {
            invocation.source().sendMessage(Component
                    .text("/isregistered <username>")
                    .color(NamedTextColor.RED)
            );
            return;
        }
        plugin
                .getDatabaseManager()
                .getUser(args[0])
                .ifPresentOrElse(
                        userModel -> invocation.source().sendMessage(Component
                                .text("%s player is registered".formatted(args[0]))
                                .color(NamedTextColor.GREEN)
                        ),
                        () -> invocation.source().sendMessage(Component
                                .text("%s player is not registered".formatted(args[0]))
                                .color(NamedTextColor.RED)
                        )
                );
    }
}
