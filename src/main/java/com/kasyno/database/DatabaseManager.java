package com.kasyno.database;

import java.sql.*;

public class DatabaseManager {
    public static void initializeTables() {
        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "balance REAL DEFAULT 1000)";

        String betsTable = "CREATE TABLE IF NOT EXISTS bets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "game_type TEXT NOT NULL, " +
                "bet_type TEXT NOT NULL, " +
                "bet_value TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "result TEXT, " +
                "timestamp DATETIME DEFAULT (datetime('now', 'localtime')), " +
                "FOREIGN KEY(username) REFERENCES users(username))";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(betsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:kasyno.db");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
