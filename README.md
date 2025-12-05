# sorter3 — CSV Sorter and Benchmark

This is a small Java Swing application that lets you load a CSV file, choose a column to sort by, run several sorting algorithms, and compare execution times.

Build (PowerShell):

```powershell
mvn package
java -jar target/sorter3-0.1.0.jar
```

Usage:
- Choose a CSV file.
- Pick a column from the dropdown.
- Check one or more algorithms and click `Run Sorted`.
- Results show execution times and a small sample of sorted rows.
