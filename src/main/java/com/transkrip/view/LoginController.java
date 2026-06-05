package com.transkrip.view;

import com.transkrip.controller.AuthController;
import com.transkrip.controller.TranskripController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblPesan;

    private final AuthController auth = AuthController.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Kosong — tidak ada init awal yang diperlukan
    }

    @FXML
    private void handleLogin() {
        String u = txtUsername.getText().trim();
        String p = txtPassword.getText();

        if (u.isEmpty() || p.isEmpty()) {
            showPesan("Username dan password tidak boleh kosong.", false);
            return;
        }

        if (auth.login(u, p)) {
            TranskripController.getInstance().loadData();
            bukaMainLayout();
        } else {
            showPesan("Username atau password salah. Silakan coba lagi.", false);
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    @FXML
    private void handleDaftar() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Daftar Akun Baru");
        dialog.setHeaderText("Buat Akun Baru");

        ButtonType btnDaftar = new ButtonType("Daftar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnDaftar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField     tfUser   = new TextField();
        PasswordField pfPass   = new PasswordField();
        PasswordField pfKonfirm = new PasswordField();

        tfUser.setPromptText("Username baru");
        pfPass.setPromptText("Password (min. 6 karakter)");
        pfKonfirm.setPromptText("Ulangi password");

        grid.add(new Label("Username:"),   0, 0); grid.add(tfUser,    1, 0);
        grid.add(new Label("Password:"),   0, 1); grid.add(pfPass,    1, 1);
        grid.add(new Label("Konfirmasi:"), 0, 2); grid.add(pfKonfirm, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result != btnDaftar) return;

            if (!pfPass.getText().equals(pfKonfirm.getText())) {
                showPesan("Password dan konfirmasi tidak cocok.", false);
                return;
            }

            String status = auth.register(tfUser.getText(), pfPass.getText());
            switch (status) {
                case "SUCCESS"            -> showPesan("Akun berhasil dibuat! Silakan login.", true);
                case "USERNAME_TAKEN"     -> showPesan("Username sudah digunakan.", false);
                case "PASSWORD_TOO_SHORT" -> showPesan("Password minimal 6 karakter.", false);
                default                   -> showPesan("Gagal membuat akun. Coba lagi.", false);
            }
        });
    }

    private void bukaMainLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/transkrip/fxml/MainLayout.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Scene scene = new Scene(root, 960, 640);
            stage.setScene(scene);
            stage.setMinWidth(780);
            stage.setMinHeight(540);
            stage.setTitle("Transkrip Nilai Pribadi");
        } catch (Exception e) {
            e.printStackTrace();
            showPesan("Gagal memuat halaman utama: " + e.getMessage(), false);
        }
    }

    private void showPesan(String pesan, boolean sukses) {
        lblPesan.setText(pesan);
        lblPesan.getStyleClass().removeAll("error-label", "success-label");
        lblPesan.getStyleClass().add(sukses ? "success-label" : "error-label");
    }
}
