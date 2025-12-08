package com.sorter3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.filechooser.FileNameExtensionFilter; // Added for CSV filter
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;

public class Main {
    private JFrame frame;
    private JTable previewTable;
    private JComboBox<String> columnBox;
    private JCheckBox insertionSortBox, shellSortBox, mergeSortBox, quickSortBox, heapSortBox;
    private JTextArea resultsArea;
    private BarChartPanel chartPanel;
    private JLabel bestLabel;
    private JToggleButton themeToggle;
    private CsvLoader.CsvTable table;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Table.rowHeight", 26);
            UIManager.put("Button.focusWidth", 2);
            UIManager.put("Component.focusWidth", 2);
        } catch (Exception ex) {
            System.err.println("FlatLaf not available, using default L&F: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> new Main().buildGui());
    }

    private void buildGui() {
        frame = new JFrame("CSV Sorter & Benchmark");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton openBtn = new JButton("Open CSV");
        openBtn.addActionListener(this::onOpen);
        top.add(openBtn);

        columnBox = new JComboBox<>();
        top.add(new JLabel("Sort by:"));
        top.add(columnBox);
        columnBox.addActionListener(ev -> {
            if (previewTable != null) previewTable.repaint();
        });

        insertionSortBox = new JCheckBox("InsertionSort", true);
        shellSortBox = new JCheckBox("ShellSort", true);
        mergeSortBox = new JCheckBox("MergeSort", true);
        quickSortBox = new JCheckBox("QuickSort", true);
        heapSortBox = new JCheckBox("HeapSort", true);

        top.add(insertionSortBox);
        top.add(shellSortBox);
        top.add(mergeSortBox);
        top.add(quickSortBox);
        top.add(heapSortBox);

        JButton runBtn = new JButton("Run Benchmark");
        runBtn.addActionListener(this::onRun);
        top.add(runBtn);

        themeToggle = new JToggleButton("Dark Mode");
        themeToggle.addItemListener(ev -> applyTheme(themeToggle.isSelected()));
        top.add(themeToggle);

        frame.add(top, BorderLayout.NORTH);

        previewTable = new JTable();
        previewTable.setDefaultRenderer(Object.class, new ColumnHighlightRenderer());

        JPanel right = new JPanel();
        right.setLayout(new BorderLayout());

        chartPanel = new BarChartPanel();
        chartPanel.setPreferredSize(new Dimension(350, 300));
        right.add(chartPanel, BorderLayout.CENTER);

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultsArea.setRows(8);
        right.add(new JScrollPane(resultsArea), BorderLayout.SOUTH);

        bestLabel = new JLabel("Best: n/a");
        bestLabel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        right.add(bestLabel, BorderLayout.NORTH);

        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(previewTable), right);
        centerSplit.setResizeWeight(0.6);
        frame.add(centerSplit, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void applyTheme(boolean dark) {
        try {
            UIManager.setLookAndFeel(dark ? new FlatDarkLaf() : new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(frame);
            frame.invalidate();
            frame.validate();
        } catch (Exception ex) {
            System.err.println("Failed to switch theme: " + ex.getMessage());
        }
    }

    private void onOpen(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(".")); 

        // Apply CSV file filter
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);


        int r = chooser.showOpenDialog(frame);
        
        if (r != JFileChooser.APPROVE_OPTION) return;
        
        File f = chooser.getSelectedFile();
        
        // Manual check for file extension
        if (!f.getName().toLowerCase().endsWith(".csv")) {
            JOptionPane.showMessageDialog(frame, 
                "Invalid file type selected. Please select a CSV file.", 
                "File Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            table = CsvLoader.load(f.getAbsolutePath());
            updatePreview();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Failed to load CSV: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updatePreview() {
        columnBox.removeAllItems();
        for (String h : table.headers) columnBox.addItem(h);
        DefaultTableModel model = new DefaultTableModel();
        for (String h : table.headers) model.addColumn(h);
        int preview = Math.min(50, table.rows.size());
        for (int i = 0; i < preview; i++) model.addRow(table.rows.get(i));
        previewTable.setModel(model);
    }

    private boolean isColumnNumeric(int colIndex) {
        if (table == null || table.rows.isEmpty()) return false;
        int checkCount = Math.min(50, table.rows.size());
        for (int i = 0; i < checkCount; i++) {
            String[] row = table.rows.get(i);
            if (colIndex >= row.length) continue; 

            String value = row[colIndex].trim();
            if (value.isEmpty()) continue;

            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private void onRun(ActionEvent e) {
        if (table == null) { JOptionPane.showMessageDialog(frame, "Open a CSV first."); return; }
        int col = columnBox.getSelectedIndex();
        if (col < 0) { JOptionPane.showMessageDialog(frame, "Select a column."); return; }

        // Numeric data check
        if (!isColumnNumeric(col)) {
            JOptionPane.showMessageDialog(frame, 
                "The selected column ('" + columnBox.getSelectedItem() + "') does not appear to contain numeric data.\n" +
                "The current sort implementations only reliably support numeric data for benchmarking.", 
                "Data Type Error", 
                JOptionPane.ERROR_MESSAGE);
            return; 
        }

        resultsArea.setText("");
        List<SortResult> results = new ArrayList<>();

        if (insertionSortBox.isSelected()) results.add(runAlgorithm("InsertionSort", table, col, InsertionSort::sort));
        if (shellSortBox.isSelected()) results.add(runAlgorithm("ShellSort", table, col, ShellSort::sort));
        if (mergeSortBox.isSelected()) results.add(runAlgorithm("MergeSort", table, col, MergeSort::sort));
        if (quickSortBox.isSelected()) results.add(runAlgorithm("QuickSort", table, col, QuickSort::sort));
        if (heapSortBox.isSelected()) results.add(runAlgorithm("HeapSort", table, col, HeapSort::sort));


        StringBuilder sb = new StringBuilder();
        for (SortResult r : results) {
            sb.append(String.format("%-15s : %6d ms\n", r.algorithm, r.durationMillis));
            int show = Math.min(5, r.sampleRows.size());
            for (int i = 0; i < show; i++) {
                sb.append("  ").append(String.join(", ", r.sampleRows.get(i))).append("\n");
            }
            sb.append("\n");
        }
        resultsArea.setText(sb.toString());
        
        chartPanel.setResults(results);
        chartPanel.repaint();
        if (!results.isEmpty()) {
            SortResult best = results.stream().min((a,b)->Long.compare(a.durationMillis,b.durationMillis)).get();
            bestLabel.setText(String.format("Best: %s (%d ms)", best.algorithm, best.durationMillis));

            BarChartPanel dialogChart = new BarChartPanel();
            dialogChart.setResults(results);
            dialogChart.setPreferredSize(new Dimension(480, 320));

            String message = String.format("Best algorithm: %s\nDuration: %d ms", best.algorithm, best.durationMillis);
            JDialog dlg = new JDialog(frame, "Run Summary", true);
            dlg.setLayout(new BorderLayout(8,8));
            JLabel lbl = new JLabel(message);
            lbl.setBorder(BorderFactory.createEmptyBorder(8,8,0,8));
            dlg.add(lbl, BorderLayout.NORTH);
            dlg.add(dialogChart, BorderLayout.CENTER);
            JButton close = new JButton("Close");
            close.addActionListener(a -> dlg.dispose());
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.add(close);
            dlg.add(bottom, BorderLayout.SOUTH);
            dlg.pack();
            dlg.setLocationRelativeTo(frame);
            dlg.setVisible(true);
            
            int ans = JOptionPane.showConfirmDialog(frame,
                    "Sort the full table using the best algorithm?",
                    "Apply Best Sort",
                    JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.YES_OPTION) {
                applyAlgorithmToTable(best.algorithm, table, col);
                updatePreview();
                JOptionPane.showMessageDialog(frame, "Table sorted using " + best.algorithm);
            }
        } else {
            bestLabel.setText("Best: n/a");
        }
    }

    private void applyAlgorithmToTable(String algorithm, CsvLoader.CsvTable table, int colIndex) {
        if (table == null) return;
        switch (algorithm) {
            case "InsertionSort":
                InsertionSort.sort(table.rows, colIndex);
                break;
            case "ShellSort":
                ShellSort.sort(table.rows, colIndex);
                break;
            case "MergeSort":
                MergeSort.sort(table.rows, colIndex);
                break;
            case "QuickSort":
                QuickSort.sort(table.rows, colIndex);
                break;
            case "HeapSort":
                HeapSort.sort(table.rows, colIndex);
                break;
            default:
                QuickSort.sort(table.rows, colIndex);
                break;
        }
    }


    private SortResult runAlgorithm(String name, CsvLoader.CsvTable table, int colIndex, AlgorithmRunner runner) {
        List<String[]> copy = new ArrayList<>(table.rows.size());
        for (String[] r : table.rows) copy.add(r.clone());

        if (name.equals("QuickSort") && copy.size() > 1000) {
             Collections.shuffle(copy);
        }

        long t0 = System.nanoTime();
        runner.run(copy, colIndex);
        long t1 = System.nanoTime();
        long ms = (t1 - t0) / 1_000_000;
        
        List<String[]> sample = new ArrayList<>();
        for (int i = 0; i < Math.min(10, copy.size()); i++) sample.add(copy.get(i)); 
        
        return new SortResult(name, ms, sample);
    }
    
    /** Simple bar chart panel to display durations per algorithm. */
    private static class BarChartPanel extends JPanel {
        private List<SortResult> results = List.of();

        public void setResults(List<SortResult> results) {
            this.results = results == null ? List.of() : results;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                int w = getWidth();
                int h = getHeight();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (results == null || results.isEmpty()) {
                    g2.setColor(Color.GRAY);
                    g2.drawString("No results to display", 10, 20);
                    return;
                }

                long max = results.stream().mapToLong(r -> Math.max(1, r.durationMillis)).max().orElse(1);
                int padding = 40;
                int chartBottom = h - padding;
                int chartTop = padding;
                int chartHeight = chartBottom - chartTop;

                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(padding, chartBottom, w - padding, chartBottom);
                
                int availableW = w - padding * 2;
                int barCount = results.size();
                int totalGapWidth = availableW / (barCount * 4); 
                int barWidth = Math.max(20, (availableW - (barCount - 1) * totalGapWidth) / barCount);
                int gap = Math.max(5, totalGapWidth);
                
                int x = padding + (availableW - (barWidth * barCount + gap * (barCount - 1))) / 2;
                
                SortResult best = results.stream().min((a,b)->Long.compare(a.durationMillis,b.durationMillis)).get();
                Color bestColor = new Color(0x2E7D32); // Green
                Color defaultColor = new Color(0x1976D2); // Blue

                for (SortResult r : results) {
                    double ratio = (double) r.durationMillis / (double) max;
                    int barH = (int) (chartHeight * ratio);
                    int y = chartBottom - barH;

                    g2.setColor(r == best ? bestColor : defaultColor);
                    g2.fillRoundRect(x, y, barWidth, Math.max(4, barH), 8, 8);
                    
                    g2.setColor(Color.DARK_GRAY);
                    String label = r.algorithm;
                    FontMetrics fm = g2.getFontMetrics();
                    int labelW = fm.stringWidth(label);
                    int lx = x + (barWidth - labelW) / 2;
                    int ly = chartBottom + fm.getHeight(); 
                    g2.drawString(label, Math.max(lx, x), ly);

                    String val = r.durationMillis + " ms";
                    int vw = fm.stringWidth(val);
                    int vx = x + (barWidth - vw) / 2;
                    int vy = Math.max(chartTop + 10, y - 6);
                    g2.setColor(Color.BLACK);
                    g2.drawString(val, Math.max(vx, x), vy);

                    x += barWidth + gap;
                }
            } finally {
                g2.dispose();
            }
        }
    }

    /** Renderer that highlights the currently selected column from the combo box. */
    private class ColumnHighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int sel = columnBox.getSelectedIndex();
            if (sel >= 0 && column == sel) {
                c.setBackground(new Color(0xFFF9C4)); // light yellow
            } else {
                if (isSelected) c.setBackground(table.getSelectionBackground());
                else c.setBackground(table.getBackground());
            }
            return c;
        }
    }

    private interface AlgorithmRunner { void run(List<String[]> rows, int colIndex); }
}