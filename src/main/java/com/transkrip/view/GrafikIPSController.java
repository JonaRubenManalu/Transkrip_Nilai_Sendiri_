package com.transkrip.view;

import com.transkrip.controller.KalkulatorNilai;
import com.transkrip.controller.TranskripController;
import com.transkrip.model.MataKuliah;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class GrafikIPSController implements Initializable, BaseController {

    @FXML private Label lblIPK;
    @FXML private Label lblSKS;
    @FXML private Label lblSemester;
    @FXML private LineChart<Number, Number> chartIPS;
    @FXML private Label lblKosong;
    @FXML private VBox  vboxDetailIPS;

    private MainLayoutController mainLayout;
    private final TranskripController tc = TranskripController.getInstance();

    @Override
    public void setMainLayout(MainLayoutController ml) {
        this.mainLayout = ml;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Isi kartu ringkasan
        double ipk = tc.hitungIPK();
        lblIPK.setText(KalkulatorNilai.formatNilai(ipk));
        lblSKS.setText(String.valueOf(tc.getTotalSKS()));

        int sem = tc.getSemesterTerakhir();
        lblSemester.setText(sem == 0 ? "-" : String.valueOf(sem));

        Map<Integer, Double> dataGrafik = tc.getDataGrafik();

        if (dataGrafik.isEmpty()) {
            chartIPS.setVisible(false);
            chartIPS.setManaged(false);
            lblKosong.setVisible(true);
            lblKosong.setManaged(true);
        } else {
            muatGrafik(dataGrafik);
            muatTabelDetail(dataGrafik);
        }
    }

    private void muatGrafik(Map<Integer, Double> data) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("IPS per Semester");

        data.forEach((semester, ips) ->
                series.getData().add(new XYChart.Data<>(semester, ips)));

        chartIPS.getData().add(series);
    }

    private void muatTabelDetail(Map<Integer, Double> data) {
        data.forEach((sem, ips) -> {
            List<MataKuliah> mkSem = tc.getMataKuliahBySemester(sem);
            int totalSKSSem = mkSem.stream().mapToInt(MataKuliah::getSks).sum();

            HBox row = new HBox(0);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setStyle(
                    "-fx-border-color: transparent transparent #2a2a42 transparent;" +
                            "-fx-border-width: 0 0 1 0;"
            );

            String warnaIPS = warnaIPS(ips);

            // Kolom Semester
            Label lSem = buatLabel("Semester " + sem, 140, "#aaaacc", false);

            // Kolom IPS
            Label lIPS = buatLabel(KalkulatorNilai.formatNilai(ips), 80, warnaIPS, true);

            // Kolom SKS
            Label lSKS = buatLabel(totalSKSSem + " SKS", 80, "#5a5a7a", false);

            // Progress bar
            ProgressBar pb = new ProgressBar(ips / 4.0);
            pb.setPrefWidth(150);
            pb.setPrefHeight(6);
            pb.getStyleClass().add("ips-progress-bar");
            HBox.setMargin(pb, new Insets(0, 14, 0, 0));
            HBox.setHgrow(pb, Priority.NEVER);

            // Kolom Keterangan
            Label lKet = buatLabel(keteranganIPS(ips), 130, warnaIPS, false);

            row.getChildren().addAll(lSem, lIPS, lSKS, pb, lKet);
            vboxDetailIPS.getChildren().add(row);
        });
    }

    private Label buatLabel(String teks, double minW, String warna, boolean bold) {
        Label l = new Label(teks);
        l.setMinWidth(minW);
        l.setStyle(
                "-fx-text-fill: " + warna + ";" +
                        "-fx-font-size: 13px;" +
                        (bold ? "-fx-font-family: 'Consolas'; -fx-font-weight: bold;" : "")
        );
        return l;
    }

    private String warnaIPS(double ips) {
        if (ips >= 3.5) return "#58d68d";
        if (ips >= 3.0) return "#1abc9c";
        if (ips >= 2.0) return "#f4a832";
        return "#e74c3c";
    }

    private String keteranganIPS(double ips) {
        if (ips >= 3.5) return "Sangat Memuaskan";
        if (ips >= 3.0) return "Memuaskan";
        if (ips >= 2.0) return "Cukup";
        return "Kurang";
    }
}
