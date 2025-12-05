package com.sorter3;

import java.util.*;

public class SortAlgorithms {

    private static Comparator<String[]> comparator(final int colIndex) {
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

    public static void javaSort(List<String[]> rows, int colIndex) {
        rows.sort(comparator(colIndex));
    }

    public static void mergeSort(List<String[]> rows, int colIndex) {
        String[][] arr = rows.toArray(new String[0][]);
        mergeSort(arr, 0, arr.length - 1, comparator(colIndex));
        rows.clear();
        Collections.addAll(rows, arr);
    }

    private static void mergeSort(String[][] a, int l, int r, Comparator<String[]> cmp) {
        if (l >= r) return;
        int m = (l + r) >>> 1;
        mergeSort(a, l, m, cmp);
        mergeSort(a, m + 1, r, cmp);
        String[][] tmp = new String[r - l + 1][];
        int i = l, j = m + 1, k = 0;
        while (i <= m && j <= r) tmp[k++] = cmp.compare(a[i], a[j]) <= 0 ? a[i++] : a[j++];
        while (i <= m) tmp[k++] = a[i++];
        while (j <= r) tmp[k++] = a[j++];
        System.arraycopy(tmp, 0, a, l, tmp.length);
    }

    public static void quickSort(List<String[]> rows, int colIndex) {
        String[][] arr = rows.toArray(new String[0][]);
        quickSort(arr, 0, arr.length - 1, comparator(colIndex));
        rows.clear();
        Collections.addAll(rows, arr);
    }

    private static void quickSort(String[][] a, int l, int r, Comparator<String[]> cmp) {
        if (l >= r) return;
        int i = l, j = r;
        String[] pivot = a[(l + r) >>> 1];
        while (i <= j) {
            while (cmp.compare(a[i], pivot) < 0) i++;
            while (cmp.compare(a[j], pivot) > 0) j--;
            if (i <= j) {
                String[] t = a[i]; a[i] = a[j]; a[j] = t;
                i++; j--;
            }
        }
        if (l < j) quickSort(a, l, j, cmp);
        if (i < r) quickSort(a, i, r, cmp);
    }

    public static void heapSort(List<String[]> rows, int colIndex) {
        String[][] a = rows.toArray(new String[0][]);
        int n = a.length;
        Comparator<String[]> cmp = comparator(colIndex);
        for (int i = n / 2 - 1; i >= 0; i--) heapify(a, n, i, cmp);
        for (int i = n - 1; i >= 0; i--) {
            String[] t = a[0]; a[0] = a[i]; a[i] = t;
            heapify(a, i, 0, cmp);
        }
        rows.clear(); Collections.addAll(rows, a);
    }

    private static void heapify(String[][] a, int n, int i, Comparator<String[]> cmp) {
        int largest = i;
        int l = 2 * i + 1, r = 2 * i + 2;
        if (l < n && cmp.compare(a[l], a[largest]) > 0) largest = l;
        if (r < n && cmp.compare(a[r], a[largest]) > 0) largest = r;
        if (largest != i) {
            String[] t = a[i]; a[i] = a[largest]; a[largest] = t;
            heapify(a, n, largest, cmp);
        }
    }

    public static void bubbleSort(List<String[]> rows, int colIndex) {
        String[][] a = rows.toArray(new String[0][]);
        Comparator<String[]> cmp = comparator(colIndex);
        int n = a.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (cmp.compare(a[j], a[j + 1]) > 0) {
                    String[] t = a[j]; a[j] = a[j + 1]; a[j + 1] = t; swapped = true;
                }
            }
            if (!swapped) break;
        }
        rows.clear(); Collections.addAll(rows, a);
    }
}
