package com.github.jenya705.cubicauth.handler;

import com.github.jenya705.cubicauth.CubicAuth;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class Limiter {

    private final CubicAuth plugin;

    @Subscribe
    public void commandExecute(CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player player)) return;
        if (plugin.getSession(player) != null) {
            int index = event.getCommand().indexOf(' ');
            String first = index == -1 ? event.getCommand() : event.getCommand().substring(0, index);
            switch (first) {
                case "login":
                case "l":
                case "reg":
                case "log":
                case "register":
                case "p":
                case "premium":
                    return;
            }
            // Forwarding to server, because it can be limbo's command
            event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
        }
    }

}
