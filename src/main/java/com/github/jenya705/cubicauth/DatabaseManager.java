package com.github.jenya705.cubicauth;

import java.sql.*;
import java.util.Optional;

/**
 * @author Jenya705
 */
public class DatabaseManager {

    private final CubicAuth plugin;

    private Connection connection;

    public DatabaseManager(CubicAuth plugin) throws ClassNotFoundException {
        this.plugin = plugin;
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    public void reload() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://%s/%s?autoReconnect=true".formatted(
                        plugin.getConfig().getProperty(CubicAuthConfig.SQL_HOST),
                        plugin.getConfig().getProperty(CubicAuthConfig.SQL_DATABASE)
                ),
                plugin.getConfig().getProperty(CubicAuthConfig.SQL_USER),
                plugin.getConfig().getProperty(CubicAuthConfig.SQL_PASSWORD)
        );
    }

    public void update(String sql, Object... objects) throws SQLException {
        synchronized (this) {
            if (objects.length == 0) {
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
            }
            else {
                PreparedStatement statement = connection.prepareStatement(sql);
                for (int i = 0; i < objects.length; ++i) {
                    statement.setObject(i + 1, objects[i]);
                }
                statement.executeUpdate();
            }
        }
    }

    public ResultSet query(String sql, Object... objects) throws SQLException {
        synchronized (this) {
            if (objects.length == 0) {
                Statement statement = connection.createStatement();
                return statement.executeQuery(sql);
            }
            else {
                PreparedStatement statement = connection.prepareStatement(sql);
                for (int i = 0; i < objects.length; ++i) {
                    statement.setObject(i + 1, objects[i]);
                }
                return statement.executeQuery();
            }
        }
    }

    public void setup() throws SQLException {
        update("""
                CREATE TABLE IF NOT EXISTS users (
                	username VARCHAR(16) NOT NULL PRIMARY KEY,
                	password_hash TEXT NOT NULL,
                	premium TINYINT(1) NOT NULL
                );
                """);
    }

    public Optional<UserModel> getUser(String username) throws SQLException {
        ResultSet resultSet = query("SELECT * FROM users WHERE username = ?;", username);
        if (resultSet.next()) {
            return Optional.of(UserModel.single(resultSet));
        }
        return Optional.empty();
    }

    public void upsertUser(UserModel userModel) throws SQLException {
        update("""
                INSERT INTO users (username, password_hash, premium)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                password_hash = ?, premium = ?;
                """,
                userModel.getUsername(), userModel.getHashPassword(), userModel.isPremium(),
                userModel.getHashPassword(), userModel.isPremium()
        );
    }

    public void deleteUser(String nickname) throws SQLException {
        update("DELETE FROM users WHERE username = ? ", nickname);
    }
}
