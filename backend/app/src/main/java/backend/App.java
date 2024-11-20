/*
 * This source file was generated by the Gradle 'init' task
 */
package backend;

import java.sql.*;
import java.util.UUID;

public class App {

    Connection conn;

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
             Statement stmt = conn.createStatement()) {

            // Create a table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS people (
                    uuid TEXT PRIMARY KEY,
                    username TEXT NOT NULL,
                    password TEXT NOT NULL,
                    code TEXT NOT NULL,
                    cources TEXT DEFAULT '[]'
                )
            """);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public boolean isTaken(String name) throws SQLException {
        String query = "SELECT 1 FROM people WHERE username = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, name.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean isReal(UUID uuid) throws SQLException {
        String query = "SELECT 1 FROM people WHERE uuid = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public void registerAccount(UUID uuid, String username, String password, String code) throws SQLException {
        if (!isReal(uuid)) {
            try (PreparedStatement ps = conn.prepareStatement(("INSERT INTO people (uuid, username, password, codes) VALUES(?)"))){
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.setString(3, password);
                ps.setString(4, code);
                ps.executeUpdate();
            }
        }
    }

    public String createUser(String name, String password, String code) throws SQLException {
        if (isTaken(name)) {
            return "taken";
        } else {
            UUID uuid = UUID.randomUUID();
            int i = 0;
            while (isReal(uuid)){
                if (i > 100){
                    break;
                }
                uuid = UUID.randomUUID();
                i++;
            }

            registerAccount(uuid, name, password, code);
        }
        return null;
    }
}
