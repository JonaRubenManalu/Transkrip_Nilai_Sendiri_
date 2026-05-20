package com.transkrip.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:transkrip.db";
    private static Connection connection = null;


    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            // Aktifkan foreign key support di SQLite
            connection.createStatement().execute("PRAGMA foreign_keys = ON;");
        }
        return connection;
    }


    public static void initializeDatabase() {
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id_user  INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT    NOT NULL UNIQUE,
                    password TEXT    NOT NULL
                );
                """;

        String createMataKuliahTable = """
                CREATE TABLE IF NOT EXISTS mata_kuliah (
                    id_mk      INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_user    INTEGER NOT NULL,
                    nama_mk    TEXT    NOT NULL,
                    sks        INTEGER NOT NULL CHECK(sks BETWEEN 1 AND 6),
                    nilai_huruf TEXT   NOT NULL,
                    semester   INTEGER NOT NULL,
                    FOREIGN KEY (id_user) REFERENCES users(id_user)
                        ON DELETE CASCADE
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createMataKuliahTable);
            System.out.println("[DB] Database berhasil diinisialisasi.");

        } catch (SQLException e) {
            System.err.println("[DB] Error inisialisasi database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error menutup koneksi: " + e.getMessage());
        }
    }
}
