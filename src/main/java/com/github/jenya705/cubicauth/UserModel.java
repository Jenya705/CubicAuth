package com.github.jenya705.cubicauth;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jenya705
 */
@Data
@SuperBuilder
public class UserModel {

    public static UserModel single(ResultSet resultSet) throws SQLException {
        return UserModel.builder()
                .username(resultSet.getString(1))
                .hashPassword(resultSet.getString(2))
                .premium(resultSet.getBoolean(3))
                .build();
    }

    private final String username;
    private final String hashPassword;
    private final boolean premium;

}
