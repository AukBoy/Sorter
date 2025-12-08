package com.sorter3;

import java.util.*;

public class HeapSort {
    public static void sort(List<String[]> rows, int colIndex) {
        String[][] a = rows.toArray(new String[0][]);
        int n = a.length;
        Comparator<String[]> cmp = ComparatorUtil.comparator(colIndex);
        for (int i = n / 2 - 1; i >= 0; i--) heapify(a, n, i, cmp);
        for (int i = n - 1; i >= 0; i--) {
            String[] t = a[0]; a[0] = a[i]; a[i] = t;
            heapify(a, i, 0, cmp);
        }
        rows.clear();
        Collections.addAll(rows, a);
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
}
