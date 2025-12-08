package com.sorter3;

import java.util.*;

public class InsertionSort {
    public static void sort(List<String[]> rows, int colIndex) {
        String[][] a = rows.toArray(new String[0][]);
        Comparator<String[]> cmp = ComparatorUtil.comparator(colIndex);
        for (int i = 1; i < a.length; i++) {
            String[] key = a[i];
            int j = i - 1;
            while (j >= 0 && cmp.compare(a[j], key) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
        rows.clear();
        Collections.addAll(rows, a);
    }
}
