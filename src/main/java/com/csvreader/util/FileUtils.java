package com.csvreader.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;

public class FileUtils {

    public static File openFileDialog(Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo CSV");

        // Configurar filtros de extensión
        FileChooser.ExtensionFilter csvFilter =
                new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv");
        FileChooser.ExtensionFilter txtFilter =
                new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt");
        FileChooser.ExtensionFilter allFilter =
                new FileChooser.ExtensionFilter("Todos los archivos (*.*)", "*.*");

        fileChooser.getExtensionFilters().addAll(csvFilter, txtFilter, allFilter);
        fileChooser.setSelectedExtensionFilter(csvFilter);

        // Establecer directorio inicial
        String userHome = System.getProperty("user.home");
        File initialDirectory = new File(userHome + "/Desktop");
        if (!initialDirectory.exists()) {
            initialDirectory = new File(userHome);
        }
        fileChooser.setInitialDirectory(initialDirectory);

        return fileChooser.showOpenDialog(ownerWindow);
    }

    public static String getFileExtension(File file) {
        if (file == null) return "";

        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}