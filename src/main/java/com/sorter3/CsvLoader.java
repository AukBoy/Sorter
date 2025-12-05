package com.sorter3;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    public static class CsvTable {
        public final String[] headers;
        public final List<String[]> rows;

        public CsvTable(String[] headers, List<String[]> rows) {
            this.headers = headers;
            this.rows = rows;
        }
    }

    public static CsvTable load(String path) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            List<String[]> all = reader.readAll();
            if (all.isEmpty()) return new CsvTable(new String[0], new ArrayList<>());
            String[] headers = all.get(0);
            List<String[]> rows = new ArrayList<>();
            for (int i = 1; i < all.size(); i++) rows.add(all.get(i));
            return new CsvTable(headers, rows);
        }
    }
}
