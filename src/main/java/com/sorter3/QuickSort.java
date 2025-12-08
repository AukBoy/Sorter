package com.sorter3;
import java.util.*;
public class QuickSort {
    public static void sort(List<String[]> rows, int colIndex) {
        String[][] arr = rows.toArray(new String[0][]);
        quickSort(arr, 0, arr.length - 1, ComparatorUtil.comparator(colIndex));
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
}