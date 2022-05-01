package com.github.jenya705.cubicauth;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.Optional;

/**
 * @author Jenya705
 */
public class DatabaseManager {

    private final CubicAuth plugin;

    private HikariDataSource dataSource;

    public DatabaseManager(CubicAuth plugin) throws ClassNotFoundException {
        this.plugin = plugin;
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    public void reload() {
        HikariConfig config = new HikariConfig();
        config.setUsername(plugin.getConfig().getProperty(CubicAuthConfig.SQL_USER));
        config.setPassword(plugin.getConfig().getProperty(CubicAuthConfig.SQL_PASSWORD));
        config.setJdbcUrl("jdbc:mysql://%s/%s".formatted(
                plugin.getConfig().getProperty(CubicAuthConfig.SQL_HOST),
                plugin.getConfig().getProperty(CubicAuthConfig.SQL_DATABASE)
        ));
        if (dataSource != null && dataSource.isRunning()) {
            dataSource.close();
        }
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void update(String sql, Object... objects) throws SQLException {
        if (objects.length == 0) {
            Statement statement = getConnection().createStatement();
            statement.executeUpdate(sql);
        }
        else {
            PreparedStatement statement = getConnection().prepareStatement(sql);
            for (int i = 0; i < objects.length; ++i) {
                statement.setObject(i + 1, objects[i]);
            }
            statement.executeUpdate();
        }
    }

    public ResultSet query(String sql, Object... objects) throws SQLException {
        if (objects.length == 0) {
            Statement statement = getConnection().createStatement();
            return statement.executeQuery(sql);
        }
        else {
            PreparedStatement statement = getConnection().prepareStatement(sql);
            for (int i = 0; i < objects.length; ++i) {
                statement.setObject(i + 1, objects[i]);
            }
            return statement.executeQuery();
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

}
