package com.kasyno.database;

import com.kasyno.player.Player;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public static void saveBet(String username, String gameType, String betType, String betValue, double amount, String result) {
        String sql = "INSERT INTO bets(username, game_type, bet_type, bet_value, amount, result) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, gameType);
            pstmt.setString(3, betType);
            pstmt.setString(4, betValue);
            pstmt.setDouble(5, amount);
            pstmt.setString(6, result);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("Błąd: Nie udało się zapisać zakładu!");
            } else {
                System.out.println("Zapisano zakład: " + username + ", " + gameType + ", " + betType);
            }
        } catch (SQLException e) {
            System.err.println("Błąd przy zapisie zakładu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<String> getLastBets(String username, int limit) {
        List<String> bets = new ArrayList<>();
        String sql = "SELECT game_type, bet_type, bet_value, amount, result, " +
                "strftime('%Y-%m-%d %H:%M:%S', timestamp) as local_time " +
                "FROM bets WHERE username = ? ORDER BY timestamp DESC LIMIT ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();


            while (rs.next()) {
                String gameType = rs.getString("game_type");
                String betType = rs.getString("bet_type");
                String betValue = rs.getString("bet_value");
                double amount = rs.getDouble("amount");
                String result = rs.getString("result");
                String timestamp = rs.getString("local_time");

                String bet = String.format("[%s] %s: %s %s (%.2f) - %s",
                        timestamp, gameType, betType, betValue, amount,
                        result != null ? result : "W trakcie");

                bets.add(bet);
            }

            if (bets.isEmpty()) {
                System.out.println("Brak zakładów dla użytkownika: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Błąd przy pobieraniu zakładów: " + e.getMessage());
            e.printStackTrace();
        }
        return bets;
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
