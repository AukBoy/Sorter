package com.sorter3;

import java.util.*;

public class ShellSort {
    public static void sort(List<String[]> rows, int colIndex) {
        String[][] a = rows.toArray(new String[0][]);
        Comparator<String[]> cmp = ComparatorUtil.comparator(colIndex);
        int n = a.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                String[] temp = a[i];
                int j = i;
                while (j >= gap && cmp.compare(a[j - gap], temp) > 0) {
                    a[j] = a[j - gap];
                    j -= gap;
                }
                a[j] = temp;
            }
        }
        rows.clear();
        Collections.addAll(rows, a);
    }
}
