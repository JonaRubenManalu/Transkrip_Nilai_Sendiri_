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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class MainLayoutController implements Initializable {

    @FXML private Label     lblUsername;
    @FXML private StackPane contentPane;
    @FXML private Button    btnDashboard;
    @FXML private Button    btnRiwayat;
    @FXML private Button    btnTambah;

    private Button activeBtn;

    // Cache node per FXML (kecuali FormMataKuliah)
    private final Map<String, CachedPage> pageCache = new HashMap<>();

    private record CachedPage(Node node, BaseController controller) {}

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblUsername.setText(AuthController.getInstance().getCurrentUsername());
        setActive(btnDashboard);
        muatHalaman("Dashboard.fxml");
    }

    @FXML public void goDashboard() { setActive(btnDashboard); muatHalaman("Dashboard.fxml"); }
    @FXML public void goRiwayat()   { setActive(btnRiwayat);   muatHalaman("RiwayatNilai.fxml"); }
    @FXML public void goTambah()    { setActive(btnTambah);    muatHalaman("FormMataKuliah.fxml"); }

    @FXML
    public void handleLogout() {
        AuthController.getInstance().logout();
        pageCache.clear();
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

    public void navigateTo(String namaFxml) {
        switch (namaFxml) {
            case "Dashboard.fxml"      -> goDashboard();
            case "RiwayatNilai.fxml"   -> goRiwayat();
            case "FormMataKuliah.fxml" -> goTambah();
        }
    }

    private void muatHalaman(String namaFile) {
        try {
            boolean isCacheable = !namaFile.equals("FormMataKuliah.fxml");
            Node node;
            BaseController ctrl;

            if (isCacheable && pageCache.containsKey(namaFile)) {
                // Ambil dari cache — tapi tetap panggil onNavigatedTo() agar data refresh
                CachedPage cached = pageCache.get(namaFile);
                node = cached.node();
                ctrl = cached.controller();
            } else {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/transkrip/fxml/" + namaFile));
                node = loader.load();
                ctrl = loader.getController() instanceof BaseController bc ? bc : null;

                if (ctrl != null) ctrl.setMainLayout(this);
                if (isCacheable && ctrl != null) pageCache.put(namaFile, new CachedPage(node, ctrl));
            }

            // KUNCI FIX: panggil onNavigatedTo() setiap kali halaman ditampilkan
            if (ctrl != null) ctrl.onNavigatedTo();

            contentPane.getChildren().setAll(node);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActive(Button btn) {
        if (activeBtn != null) {
            activeBtn.getStyleClass().removeAll("sidebar-btn-active");
            if (!activeBtn.getStyleClass().contains("sidebar-btn"))
                activeBtn.getStyleClass().add("sidebar-btn");
        }
        btn.getStyleClass().remove("sidebar-btn");
        if (!btn.getStyleClass().contains("sidebar-btn-active"))
            btn.getStyleClass().add("sidebar-btn-active");
        activeBtn = btn;
    }
}
