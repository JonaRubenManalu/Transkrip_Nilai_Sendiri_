package com.transkrip.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:transkrip.db";
    private static volatile Connection connection = null;
    private static final Object LOCK = new Object();

    public static Connection getConnection() throws SQLException {
        synchronized (LOCK) {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                applyPragmas(connection);
            }
            return connection;
        }
    }

    private static void applyPragmas(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("PRAGMA journal_mode = WAL;");       // tulis lebih cepat
            stmt.execute("PRAGMA cache_size = -8000;");       // ~8 MB page cache
            stmt.execute("PRAGMA synchronous = NORMAL;");     // lebih cepat, masih aman
            stmt.execute("PRAGMA temp_store = MEMORY;");      // tabel temp di RAM
        }
    }

    public static void initializeDatabase() {
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id_user  INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT    NOT NULL UNIQUE,
                    password TEXT    NOT NULL
                );
                """;

        // OPTIMASI: index pada id_user & semester agar filter lebih cepat
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

        String createIndexUser     = "CREATE INDEX IF NOT EXISTS idx_mk_user ON mata_kuliah(id_user);";
        String createIndexSemester = "CREATE INDEX IF NOT EXISTS idx_mk_user_sem ON mata_kuliah(id_user, semester);";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createMataKuliahTable);
            stmt.execute(createIndexUser);
            stmt.execute(createIndexSemester);
            System.out.println("[DB] Database berhasil diinisialisasi.");

        } catch (SQLException e) {
            System.err.println("[DB] Error inisialisasi database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        synchronized (LOCK) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    connection = null;
                    System.out.println("[DB] Koneksi database ditutup.");
                }
            } catch (SQLException e) {
                System.err.println("[DB] Error menutup koneksi: " + e.getMessage());
            }
        }
    }
}
