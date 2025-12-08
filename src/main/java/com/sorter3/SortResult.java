package com.sorter3;

import java.util.List;

/**
 * Stores the result of a single sorting run for benchmarking.
 */
public class SortResult {
    public final String algorithm;
    public final long durationMillis;
    public final List<String[]> sampleRows;

    public SortResult(String algorithm, long durationMillis, List<String[]> sampleRows) {
        this.algorithm = algorithm;
        this.durationMillis = durationMillis;
        this.sampleRows = sampleRows;
    }
}