package com.github.jenya705.cubicauth;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Jenya705
 */
@Data
@SuperBuilder
public class UserLoginSession {

    private final UserModel userModel;

    private RegisteredServer thenConnect;
    private List<ScheduledTask> tasks;

}
