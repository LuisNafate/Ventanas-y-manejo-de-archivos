package com.csvreader.service;

import com.csvreader.model.CSVData;
import java.io.*;
import java.util.*;

public class CSVReaderService {
    private static final String DELIMITER = ",";
    private static final String QUOTE = "\"";

    public CSVData readCSVFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("El archivo no existe");
        }

        List<String> headers = new ArrayList<>();
        List<List<String>> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                List<String> row = parsCSVLine(line);

                if (isFirstLine) {
                    headers.addAll(row);
                    isFirstLine = false;
                } else {
                    // Asegurar que todas las filas tengan el mismo número de columnas
                    while (row.size() < headers.size()) {
                        row.add("");
                    }
                    data.add(row);
                }
            }
        }

        return new CSVData(headers, data, file.getName());
    }

    private List<String> parsCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Comillas dobles escapadas
                    currentField.append('"');
                    i++; // Saltar la siguiente comilla
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }

        // Agregar el último campo
        result.add(currentField.toString().trim());

        return result;
    }

    public boolean isValidCSVFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }

        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".csv") || fileName.endsWith(".txt");
    }
}
