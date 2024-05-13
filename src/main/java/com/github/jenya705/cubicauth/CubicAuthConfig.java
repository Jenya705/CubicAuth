package com.github.jenya705.cubicauth;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;

/**
 * @author Jenya705
 */
public class CubicAuthConfig implements SettingsHolder {

    @Comment("Mysql user")
    public static final Property<String> SQL_USER = PropertyInitializer
            .newProperty("sql.user", "root");

    @Comment("Mysql user password")
    public static final Property<String> SQL_PASSWORD = PropertyInitializer
            .newProperty("sql.password", "1");

    @Comment("Mysql host")
    public static final Property<String> SQL_HOST = PropertyInitializer
            .newProperty("sql.host", "localhost:3306");

    @Comment("Mysql database")
    public static final Property<String> SQL_DATABASE = PropertyInitializer
            .newProperty("sql.database", "auth");

    @Comment("Name of the limbo server")
    public static final Property<String> LIMBO_SERVER = PropertyInitializer
            .newProperty("limbo", "reg-limbo");

    @Comment({
            "Interval of time (in seconds) when command message will be repeated",
            "When player is not registering, message.register_notify will be sent otherwise message.login_notify"
    })
    public static final Property<Integer> AUTH_MESSAGE_REPEAT = PropertyInitializer
            .newProperty("auth_message_repeat", 10);

    @Comment({
            "Interval of time (in seconds) when player will be kicked because of exceeding time",
            "Kick message is message.authentication_time_exceeded"
    })
    public static final Property<Integer> AUTHENTICATION_TIME = PropertyInitializer
            .newProperty("authentication_time", 60);

    @Comment("Maximum attempts. If more then player will be kicked with reason message.maximum_attempts")
    public static final Property<Integer> MAXIMUM_ATTEMPTS = PropertyInitializer
            .newProperty("maximum_attempts", 5);

    public static final Property<String> ALREADY_AUTHENTICATED = PropertyInitializer
            .newProperty("message.already_authenticated", "Auth >> You are already authenticated!");

    public static final Property<String> ALREADY_REGISTERED = PropertyInitializer
            .newProperty("message.already_registered", "Auth >> You are already registered! Use /login <password> to login");

    public static final Property<String> AUTHENTICATION_SUCCESS = PropertyInitializer
            .newProperty("message.authentication_success", "Auth >> Successfully authenticated!");

    public static final Property<String> BAD_PASSWORD = PropertyInitializer
            .newProperty("message.bad_password", "Auth >> Password is not right!");

    public static final Property<String> REGISTER_NOTIFY = PropertyInitializer
            .newProperty("message.register_notify", "Auth >> Register using /register <password> <password repeat>");

    public static final Property<String> LOGIN_NOTIFY = PropertyInitializer
            .newProperty("message.login_notify", "Auth >> Login using /login <password>");

    public static final Property<String> AUTHENTICATION_TIME_EXCEEDED = PropertyInitializer
            .newProperty("message.authentication_time_exceeded", "Time to login exceeded!");

    public static final Property<String> REGISTER_COMMAND = PropertyInitializer
            .newProperty("message.register_command", "/register <password> <password repeat>");

    public static final Property<String> LOGIN_COMMAND = PropertyInitializer
            .newProperty("message.login_command", "/login <password>");

    public static final Property<String> PREMIUM_COMMAND = PropertyInitializer
            .newProperty("message.premium_command", "/premium");

    public static final Property<String> PREMIUM_COMMAND_PASSWORD = PropertyInitializer
            .newProperty("message.premium_command_password", "/premium <password>");

    public static final Property<String> MAXIMUM_ATTEMPTS_REASON = PropertyInitializer
            .newProperty("message.maximum_attempts", "Too many attempts!");

    public static final Property<String> SUCCESS = PropertyInitializer
            .newProperty("message.success", "Auth >> Successfully!");

    public static final Property<String> NEED_REGISTER = PropertyInitializer
            .newProperty("message.need_register", "You need to register to play!");

    public static final Property<String> CHANGE_PASSWORD = PropertyInitializer
            .newProperty("message.change_password", "/changepassword <current password> <new password>");

    public static final Property<String> CONTACT_ADMINISTRATOR = PropertyInitializer
            .newProperty("message.contact_administrator", "Contact to administrator to fix the problem!");

    public static final Property<String> BAD_CHARACTERS = PropertyInitializer
            .newProperty("message.bad_characters", "Your nickname contains bad characters!");

}
