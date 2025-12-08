package com.sorter3;

import java.util.Comparator;

public class ComparatorUtil {
    public static Comparator<String[]> comparator(final int colIndex) {
        return (a, b) -> {
            String sa = colIndex < a.length ? a[colIndex] : "";
            String sb = colIndex < b.length ? b[colIndex] : "";
            Double da = tryParseDouble(sa);
            Double db = tryParseDouble(sb);
            if (da != null && db != null) return Double.compare(da, db);
            return sa.compareTo(sb);
        };
    }

    private static Double tryParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }
}
