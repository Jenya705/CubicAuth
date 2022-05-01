package com.github.jenya705.cubicauth.handler;

import com.github.jenya705.cubicauth.CubicAuth;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import lombok.RequiredArgsConstructor;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class DisconnectHandler {

    private final CubicAuth plugin;

    @Subscribe
    public void disconnect(DisconnectEvent event) {
        plugin.forceRemoveSession(event.getPlayer());
    }

}
