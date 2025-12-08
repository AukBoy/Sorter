package com.sorter3;

import java.util.*;

public class MergeSort {
    public static void sort(List<String[]> rows, int colIndex) {
        String[][] arr = rows.toArray(new String[0][]);
        mergeSort(arr, 0, arr.length - 1, ComparatorUtil.comparator(colIndex));
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
}
