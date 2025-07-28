package com.csvreader.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;

public class CSVData {
    private final ObservableList<CSVRow> rows;
    private final List<String> headers;
    private String fileName;

    public CSVData() {
        this.rows = FXCollections.observableArrayList();
        this.headers = new ArrayList<>();
    }

    public CSVData(List<String> headers, List<List<String>> data, String fileName) {
        this();
        this.fileName = fileName;
        this.headers.addAll(headers);

        for (List<String> rowData : data) {
            rows.add(new CSVRow(rowData));
        }
    }

    public ObservableList<CSVRow> getRows() {
        return rows;
    }

    public List<String> getHeaders() {
        return new ArrayList<>(headers);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return headers.size();
    }

    public void clear() {
        rows.clear();
        headers.clear();
        fileName = null;
    }
}