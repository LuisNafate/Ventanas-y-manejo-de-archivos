package com.csvreader.controller;

import com.csvreader.model.CSVData;
import com.csvreader.model.CSVRow;
import com.csvreader.service.CSVReaderService;
import com.csvreader.service.CSVPrinterService;
import com.csvreader.util.FileUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public VBox dropIndicator;
    @FXML private Button btnOpenFile;
    @FXML private Button btnClear;
    @FXML private TableView<CSVRow> tableView;
    @FXML private Label lblStatus;
    @FXML private Label lblFileInfo;
    @FXML private ProgressBar progressBar;

    private CSVReaderService csvReaderService;
    private CSVPrinterService csvPrinterService;
    private CSVData currentData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        setupUI();
        setupEventHandlers();
    }

    private void initializeServices() {
        csvReaderService = new CSVReaderService();
        csvPrinterService = new CSVPrinterService();
    }

    private void setupUI() {
        // Configurar estado inicial
        btnClear.setDisable(true);
        progressBar.setVisible(false);
        lblStatus.setText("Arrastra un archivo CSV aquí o usa el botón");
        lblFileInfo.setText("");

        // Configurar efectos hover para botones
        setupButtonEffects();
    }

    private void setupButtonEffects() {
        // Efecto hover para botón principal
        btnOpenFile.setOnMouseEntered(e -> {
            btnOpenFile.setStyle(btnOpenFile.getStyle() + "; -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });
        btnOpenFile.setOnMouseExited(e -> {
            btnOpenFile.setStyle(btnOpenFile.getStyle().replace("; -fx-scale-x: 1.02; -fx-scale-y: 1.02;", ""));
        });

        // Efecto hover para botón limpiar
        btnClear.setOnMouseEntered(e -> {
            if (!btnClear.isDisabled()) {
                btnClear.setStyle(btnClear.getStyle() + "; -fx-background-color: rgba(255,255,255,0.3);");
            }
        });
        btnClear.setOnMouseExited(e -> {
            if (!btnClear.isDisabled()) {
                btnClear.setStyle(btnClear.getStyle().replace("; -fx-background-color: rgba(255,255,255,0.3);", ""));
            }
        });
    }

    private void setupEventHandlers() {
        btnOpenFile.setOnAction(e -> openFile());
        btnClear.setOnAction(e -> clearData());
    }

    @FXML
    private void openFile() {
        Stage stage = (Stage) btnOpenFile.getScene().getWindow();
        File selectedFile = FileUtils.openFileDialog(stage);

        if (selectedFile != null) {
            if (!csvReaderService.isValidCSVFile(selectedFile)) {
                showAlert("Error", "Por favor selecciona un archivo CSV válido (.csv o .txt)");
                return;
            }

            loadCSVFile(selectedFile);
        }
    }

    private void loadCSVFile(File file) {
        // Mostrar progreso
        progressBar.setVisible(true);
        lblStatus.setText("Cargando " + file.getName() + "...");
        btnOpenFile.setDisable(true);

        // Crear tarea en segundo plano
        Task<CSVData> loadTask = new Task<CSVData>() {
            @Override
            protected CSVData call() throws Exception {
                return csvReaderService.readCSVFile(file);
            }

            @Override
            protected void succeeded() {
                currentData = getValue();
                displayLoadedData();
                updateUIAfterLoad(true, "Archivo cargado exitosamente", file);
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                String errorMessage = "Error al cargar el archivo: " +
                        (exception != null ? exception.getMessage() : "Error desconocido");
                updateUIAfterLoad(false, errorMessage, null);
                showAlert("Error de carga", errorMessage);
            }
        };

        // Ejecutar tarea
        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private void displayLoadedData() {
        if (currentData != null) {
            // Mostrar datos en la tabla
            csvPrinterService.displayDataInTable(tableView, currentData);
        }
    }

    private void updateUIAfterLoad(boolean success, String statusMessage, File file) {
        progressBar.setVisible(false);
        lblStatus.setText(statusMessage);
        btnOpenFile.setDisable(false);

        if (success && currentData != null) {
            btnClear.setDisable(false);

            // Actualizar información del archivo
            String fileInfo = String.format("%d filas × %d columnas | %s",
                    currentData.getRowCount(),
                    currentData.getColumnCount(),
                    FileUtils.formatFileSize(file.length()));
            lblFileInfo.setText(fileInfo);

            // Cambiar estilo del botón limpiar cuando hay datos
            btnClear.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; " +
                    "-fx-border-color: rgba(255,255,255,0.7); -fx-border-width: 1; " +
                    "-fx-border-radius: 20; -fx-background-radius: 20;");
        }
    }

    @FXML
    private void clearData() {
        // Limpiar datos
        if (currentData != null) {
            currentData.clear();
        }
        currentData = null;

        // Limpiar interfaz
        tableView.getItems().clear();
        tableView.getColumns().clear();
        lblFileInfo.setText("");

        // Actualizar estado de botones
        btnClear.setDisable(true);
        btnClear.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.7); " +
                "-fx-border-color: rgba(255,255,255,0.5); -fx-border-width: 1; " +
                "-fx-border-radius: 20; -fx-background-radius: 20;");

        lblStatus.setText("Arrastra un archivo CSV aquí o usa el botón");

        // Efecto de limpieza visual
        tableView.setStyle("-fx-background-color: transparent;");
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(100),
                        e -> tableView.setStyle(""))
        );
        timeline.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Personalizar el estilo del alert para que combine con la interfaz
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");

        alert.showAndWait();
    }
}