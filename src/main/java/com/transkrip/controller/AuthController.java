package com.transkrip.controller;

import com.transkrip.model.User;
import com.transkrip.model.UserDAO;


public class AuthController {

    private static AuthController instance;
    private final UserDAO userDAO;

    // Session: menyimpan user yang sedang login
    private User currentUser;

    // ── Singleton ────────────────────────────────────────────────────────────
    private AuthController() {
        this.userDAO = new UserDAO();
    }

    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    public boolean login(String username, String password) {
        // Validasi input tidak kosong
        if (username == null || username.isBlank()) return false;
        if (password == null || password.isBlank()) return false;

        User user = userDAO.login(username.trim(), password);
        if (user != null) {
            this.currentUser = user;
            System.out.println("[Auth] Login berhasil: " + user.getUsername());
            return true;
        }
        System.out.println("[Auth] Login gagal untuk username: " + username);
        return false;
    }

    // ── Register ──────────────────────────────────────────────────────────────
    public String register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return "INPUT_INVALID";
        }
        if (password.length() < 6) {
            return "PASSWORD_TOO_SHORT";
        }
        if (userDAO.isUsernameExists(username.trim())) {
            return "USERNAME_TAKEN";
        }
        boolean success = userDAO.register(username.trim(), password);
        return success ? "SUCCESS" : "ERROR";
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    public void logout() {
        System.out.println("[Auth] Logout: " + (currentUser != null ? currentUser.getUsername() : "-"));
        this.currentUser = null;
    }

    // ── Getter Session ────────────────────────────────────────────────────────
    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getIdUser() : -1;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "";
    }
}
