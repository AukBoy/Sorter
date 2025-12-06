package com.sorter3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;


public class Main {
    private JFrame frame;
    private JTable previewTable;
    private JComboBox<String> columnBox;
    private JCheckBox javaSortBox, quickSortBox, mergeSortBox, heapSortBox, bubbleSortBox;
    private JTextArea resultsArea;
    private BarChartPanel chartPanel;
    private JLabel bestLabel;
    private JToggleButton themeToggle;
    private CsvLoader.CsvTable table;

    public static void main(String[] args) {
        
        System.out.println("Default main executed.");
    }

}
