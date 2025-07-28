package com.csvreader.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.List;
import java.util.ArrayList;

public class CSVRow {
    private final List<StringProperty> values;

    public CSVRow(List<String> rowData) {
        this.values = new ArrayList<>();
        for (String value : rowData) {
            this.values.add(new SimpleStringProperty(value != null ? value : ""));
        }
    }

    public StringProperty getValueProperty(int index) {
        if (index >= 0 && index < values.size()) {
            return values.get(index);
        }
        return new SimpleStringProperty("");
    }

    public String getValue(int index) {
        return getValueProperty(index).get();
    }

    public int getColumnCount() {
        return values.size();
    }

    public List<String> getValues() {
        List<String> result = new ArrayList<>();
        for (StringProperty prop : values) {
            result.add(prop.get());
        }
        return result;
    }
}
