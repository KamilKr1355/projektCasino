package com.kasyno.database;

import com.kasyno.player.Player;
import java.sql.*;

import static com.kasyno.database.DatabaseManager.connect;

public class UserDAO {
    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "balance REAL DEFAULT 1000)";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean register(String username, String password) {
        String sql = "INSERT INTO users(username, password, balance) VALUES(?, ?, 1000)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static Player login(String username, String password) {
        String sql = "SELECT balance FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Player player = new Player(username);
                player.setBalance(rs.getDouble("balance"));
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updatePlayerBalance(String login, double balance) {
        String sql = "UPDATE users SET balance = ? WHERE username = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, balance);
            pstmt.setString(2, login);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Błąd przy zapisie balansu: " + e.getMessage());
        }
    }

}
