package com.transkrip.view;

import com.transkrip.controller.KalkulatorNilai;
import com.transkrip.controller.TranskripController;
import com.transkrip.model.MataKuliah;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RiwayatNilaiController implements Initializable, BaseController {

    @FXML private ComboBox<String>              cmbFilter;
    @FXML private TableView<MataKuliah>         tabelRiwayat;
    @FXML private TableColumn<MataKuliah, String>  colNama;
    @FXML private TableColumn<MataKuliah, Integer> colSKS;
    @FXML private TableColumn<MataKuliah, String>  colNilai;
    @FXML private TableColumn<MataKuliah, Double>  colBobot;
    @FXML private TableColumn<MataKuliah, Integer> colSemester;
    @FXML private TableColumn<MataKuliah, Void>    colAksi;
    @FXML private Label lblFooterIPS;

    private MainLayoutController mainLayout;
    private final TranskripController tc = TranskripController.getInstance();

    @Override
    public void setMainLayout(MainLayoutController ml) {
        this.mainLayout = ml;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupKolom();
        isiDropdownFilter();
        refreshTabel("Semua Semester");
    }

    private void setupKolom() {
        colNama.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getNamaMk()));
        colSKS.setCellValueFactory(d ->
            new SimpleIntegerProperty(d.getValue().getSks()).asObject());
        colNilai.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getNilaiHuruf()));
        colBobot.setCellValueFactory(d ->
            new SimpleDoubleProperty(d.getValue().getBobotAngka()).asObject());
        colSemester.setCellValueFactory(d ->
            new SimpleIntegerProperty(d.getValue().getSemester()).asObject());

        // Kolom Aksi dengan tombol Edit dan Hapus
        colAksi.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit  = new Button("Edit");
            private final Button btnHapus = new Button("Hapus");
            private final HBox   hbox     = new HBox(6, btnEdit, btnHapus);
            {
                hbox.setAlignment(Pos.CENTER);
                btnEdit.getStyleClass().add("btn-warning");
                btnHapus.getStyleClass().add("btn-danger");

                btnEdit.setOnAction(e -> {
                    MataKuliah mk = getTableView().getItems().get(getIndex());
                    bukaFormEdit(mk);
                });

                btnHapus.setOnAction(e -> {
                    MataKuliah mk = getTableView().getItems().get(getIndex());
                    konfirmasiHapus(mk);
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void isiDropdownFilter() {
        cmbFilter.getItems().clear();
        cmbFilter.getItems().add("Semua Semester");
        tc.getDaftarSemester().forEach(s ->
            cmbFilter.getItems().add("Semester " + s));
        cmbFilter.setValue("Semua Semester");
    }

    @FXML
    private void handleFilter() {
        refreshTabel(cmbFilter.getValue());
    }

    private void refreshTabel(String filter) {
        List<MataKuliah> data;

        if (filter == null || filter.equals("Semua Semester")) {
            data = tc.getAllMataKuliah();
            lblFooterIPS.setText(
                "IPK Kumulatif: " + KalkulatorNilai.formatNilai(tc.hitungIPK()) +
                "   |   Total SKS: " + tc.getTotalSKS());
        } else {
            int sem = Integer.parseInt(filter.replace("Semester ", ""));
            data    = tc.getMataKuliahBySemester(sem);
            lblFooterIPS.setText(
                "IPS Semester " + sem + ": " +
                KalkulatorNilai.formatNilai(tc.hitungIPS(sem)));
        }

        tabelRiwayat.setItems(FXCollections.observableArrayList(data));
    }

    private void bukaFormEdit(MataKuliah mk) {
        tc.setMataKuliahEdit(mk);
        if (mainLayout != null) mainLayout.goTambah();
    }

    private void konfirmasiHapus(MataKuliah mk) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Mata Kuliah");
        alert.setContentText("Yakin ingin menghapus \"" + mk.getNamaMk() + "\"?");

        Optional<ButtonType> hasil = alert.showAndWait();
        if (hasil.isPresent() && hasil.get() == ButtonType.OK) {
            tc.deleteMataKuliah(mk.getIdMk());
            isiDropdownFilter();
            refreshTabel(cmbFilter.getValue());
        }
    }

    @FXML
    private void goTambah() {
        if (mainLayout != null) mainLayout.goTambah();
    }
}
