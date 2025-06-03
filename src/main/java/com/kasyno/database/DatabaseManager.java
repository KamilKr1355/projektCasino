package com.kasyno.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    public static Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:kasyno.db");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
