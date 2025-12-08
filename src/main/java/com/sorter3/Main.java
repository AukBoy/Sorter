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
