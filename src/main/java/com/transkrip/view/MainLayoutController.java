package com.transkrip.view;

import com.transkrip.controller.AuthController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    @FXML private Label     lblUsername;
    @FXML private StackPane contentPane;
    @FXML private Button    btnDashboard;
    @FXML private Button    btnRiwayat;
    @FXML private Button    btnTambah;

    private Button activeBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblUsername.setText(AuthController.getInstance().getCurrentUsername());
        // Buka Dashboard saat pertama kali
        setActive(btnDashboard);
        muatHalaman("Dashboard.fxml");
    }

    @FXML
    public void goDashboard() {
        setActive(btnDashboard);
        muatHalaman("Dashboard.fxml");
    }

    @FXML
    public void goRiwayat() {
        setActive(btnRiwayat);
        muatHalaman("RiwayatNilai.fxml");
    }

    @FXML
    public void goTambah() {
        setActive(btnTambah);
        muatHalaman("FormMataKuliah.fxml");
    }



    @FXML
    public void handleLogout() {
        AuthController.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/transkrip/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.setScene(new Scene(root, 480, 560));
            stage.setMinWidth(420);
            stage.setMinHeight(480);
            stage.setTitle("Transkrip Nilai Pribadi — Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Dipakai child controller untuk navigasi
    public void navigateTo(String namaFxml) {
        switch (namaFxml) {
            case "Dashboard.fxml"       -> goDashboard();
            case "RiwayatNilai.fxml"    -> goRiwayat();
            case "FormMataKuliah.fxml"  -> goTambah();
        }
    }

    private void muatHalaman(String namaFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/transkrip/fxml/" + namaFile));
            Node node = loader.load();

            // Inject referensi MainLayoutController ke child controller
            Object ctrl = loader.getController();
            if (ctrl instanceof BaseController bc) {
                bc.setMainLayout(this);
            }

            contentPane.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActive(Button btn) {
        // Reset tombol sebelumnya
        if (activeBtn != null) {
            activeBtn.getStyleClass().removeAll("sidebar-btn-active");
            if (!activeBtn.getStyleClass().contains("sidebar-btn")) {
                activeBtn.getStyleClass().add("sidebar-btn");
            }
        }
        // Set tombol aktif
        btn.getStyleClass().remove("sidebar-btn");
        if (!btn.getStyleClass().contains("sidebar-btn-active")) {
            btn.getStyleClass().add("sidebar-btn-active");
        }
        activeBtn = btn;
    }
}
