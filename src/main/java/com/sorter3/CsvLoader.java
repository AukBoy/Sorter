package com.sorter3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    public static class CsvTable {
        public final List<String> headers;
        public final List<String[]> rows;

        public CsvTable(List<String> headers, List<String[]> rows) {
            this.headers = headers;
            this.rows = rows;
        }
    }

    public static CsvTable load(String filePath) throws IOException {
        List<String> headers = new ArrayList<>();
        List<String[]> rows = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            // Read Header
            if ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                for (String part : parts) headers.add(part.trim());
            } else {
                throw new IOException("CSV file is empty.");
            }

            // Read Rows
            while ((line = br.readLine()) != null) {
                // Simple CSV split
                rows.add(line.split(",", headers.size())); 
            }
        }

        return new CsvTable(headers, rows);
    }
}