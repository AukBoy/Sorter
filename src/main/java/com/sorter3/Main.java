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

        JButton runBtn = new JButton("Run Sort");
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

    private void applyTheme(boolean dark) {// GUI layout- dark mode and light mode
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

    