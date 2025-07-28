package com.csvreader.service;

import com.csvreader.model.CSVData;
import com.csvreader.model.CSVRow;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class CSVPrinterService {

    public void displayDataInTable(TableView<CSVRow> tableView, CSVData csvData) {
        if (csvData == null || tableView == null) {
            return;
        }

        // Limpiar columnas existentes
        tableView.getColumns().clear();

        // Crear columnas dinámicamente
        List<String> headers = csvData.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            final int columnIndex = i;

            TableColumn<CSVRow, String> column = new TableColumn<>(headers.get(i));
            column.setMinWidth(100);
            column.setPrefWidth(150);

            // Configurar cell value factory
            column.setCellValueFactory(cellData ->
                    cellData.getValue().getValueProperty(columnIndex)
            );

            tableView.getColumns().add(column);
        }

        // Establecer los datos
        tableView.setItems(csvData.getRows());

        // Ajustar el ancho de las columnas automáticamente
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public String generateSummary(CSVData csvData) {
        if (csvData == null) {
            return "No hay datos para mostrar";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Archivo: ").append(csvData.getFileName()).append("\n");
        summary.append("Filas: ").append(csvData.getRowCount()).append("\n");
        summary.append("Columnas: ").append(csvData.getColumnCount()).append("\n");
        summary.append("Encabezados: ").append(String.join(", ", csvData.getHeaders()));

        return summary.toString();
    }

    public void printToConsole(CSVData csvData) {
        if (csvData == null) {
            System.out.println("No hay datos para imprimir");
            return;
        }

        System.out.println("=== Datos del CSV ===");
        System.out.println(generateSummary(csvData));
        System.out.println("\n=== Contenido ===");

        // Imprimir encabezados
        System.out.println(String.join(" | ", csvData.getHeaders()));
        System.out.println("-".repeat(csvData.getHeaders().size() * 15));

        // Imprimir filas (máximo 10 para no saturar la consola)
        int rowCount = Math.min(csvData.getRowCount(), 10);
        for (int i = 0; i < rowCount; i++) {
            CSVRow row = csvData.getRows().get(i);
            System.out.println(String.join(" | ", row.getValues()));
        }

        if (csvData.getRowCount() > 10) {
            System.out.println("... y " + (csvData.getRowCount() - 10) + " filas más");
        }
    }
}
