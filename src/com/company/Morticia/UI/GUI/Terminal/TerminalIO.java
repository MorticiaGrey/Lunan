package com.company.Morticia.UI.GUI.Terminal;

import com.company.Morticia.UI.GUI.MainFrame;

import javax.swing.*;
import java.util.ArrayList;

public class TerminalIO {
    private static final int cmpn = 0; // Component number, codes for output
    protected static ArrayList<String> input = new ArrayList<>(); // TerminalGUI will dump input here
    protected static volatile boolean inputAdded = false;
    public static String terminalPrefix = "<html>"; // Copy of the data in prefix display

    /**
     * Prints a line to the terminal, acts similarly to System.out,println()
     *
     * @param arg Thing to be printed to terminal
     */
    public static synchronized void println(Object arg) {
        JLabel label = ((JLabel) TerminalGUI.centerPanel.getComponent(cmpn));
        String currText = label.getText();
        label.setText(currText + "<br>" + ((TextWrappingJLabel) label).wrapText(arg.toString()));
        TerminalGUI.scrollToBottom();
    }

    /**
     * Prints text to the terminal, acts similarly to System.out.print()
     *
     * @param arg Thing to be printed to terminal
     */
    public static synchronized void print(Object arg) {
        JLabel label = ((JLabel) TerminalGUI.centerPanel.getComponent(cmpn));
        String currText = label.getText();
        label.setText(currText + ((TextWrappingJLabel) label).wrapText(arg.toString()));
        TerminalGUI.scrollToBottom();
    }

    public static synchronized String nextLine() {
        TerminalGUI.inputRequested = true;
        while (input.size() < 1) {
            SwingUtilities.updateComponentTreeUI(TerminalGUI.userInputPanel);
        }
        TerminalGUI.inputRequested = false;
        String buffer = input.get(0);
        input.remove(0);
        return buffer;
    }

    public static synchronized String nextLine(String in) {
        TerminalGUI.inputRequested = true;
        TerminalGUI.prefixDisplay.setText(in);
        while (!inputAdded) {
            Thread.onSpinWait();
            //SwingUtilities.updateComponentTreeUI(TerminalGUI.userInputPanel);
            //SwingUtilities.updateComponentTreeUI(TerminalGUI.centerPanel);
        }
        inputAdded = false;
        TerminalGUI.inputRequested = false;
        String buffer = input.get(0);
        input.remove(0);
        return buffer;
    }

    /**
     * Clears the terminal of all text
     */
    public static synchronized void clearTerminal() {
        ((JLabel) TerminalGUI.centerPanel.getComponent(cmpn)).setText("<html>");
    }

    private static String prefix = "";

    /**
     * Sets the prefix shown to the user on the terminal GUI
     *
     * @param iPrefix New prefix
     */
    public static synchronized void setPrefix(String iPrefix) {
        prefix = iPrefix;
        TerminalGUI.prefixDisplay.setText("<html>" + prefix + "</html>");
        terminalPrefix = "<html>" + prefix;
    }

    /**
     * Gets the prefix currently being displayed
     *
     * @return Current prefix
     */
    public static synchronized String getPrefix() {
        return prefix;
    }

    public static String colorReset = "<font color=white>";
    public static synchronized String getColor(String hex) {
        return "<font color=" + hex + ">";
    }

    public static synchronized  String wrapInColor(String text, String hex) {
        return getColor(hex) + text + colorReset;
    }
}
