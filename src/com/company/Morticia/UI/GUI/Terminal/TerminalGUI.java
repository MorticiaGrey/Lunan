package com.company.Morticia.UI.GUI.Terminal;

import com.company.Morticia.Gamedata.Gamedata;
import com.company.Morticia.UI.UI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class TerminalGUI implements MouseWheelListener, KeyListener {
    public static JPanel centerPanel = new JPanel();
    public static JPanel userInputPanel = new JPanel();

    public static JLabel prefixDisplay = new JLabel("<html> ");

    public static boolean inputRequested = false;
    public static int fontSize = 12;
    public static int fontSizeQuantum = 2;

    public static List<String> input = new ArrayList<>();
    public static String currInput = "";
    public static int inputIndex = -1;

    /**
     * Starts the terminal interface
     */
    public static void start() {
        JFrame frame = UI.mainFrame.frame;

        centerPanel.setLayout(new BorderLayout());
        userInputPanel.setLayout(new BorderLayout());

        centerPanel.setBackground(Color.BLACK);
        userInputPanel.setBackground(Color.BLACK);

        JLabel outputDisplay = new JLabel("<html>");
        outputDisplay.setHorizontalAlignment(SwingConstants.LEFT);
        outputDisplay.setVerticalAlignment(SwingConstants.BOTTOM);
        outputDisplay.setBackground(Color.BLACK);
        outputDisplay.setForeground(Color.WHITE);

        outputDisplay.addMouseWheelListener(new TerminalGUI());

        centerPanel.add(outputDisplay, BorderLayout.CENTER);

        JTextField inputField = new JTextField() {
            @Override public void setBorder(Border border) {
                // No border
            }
        };
        inputField.setCaretColor(Color.WHITE);

        userInputPanel.add(inputField, BorderLayout.CENTER);
        userInputPanel.add(prefixDisplay, BorderLayout.WEST);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // New input processing, called when enter is pressed
                if (!inputField.getText().isBlank()) {
                    if (inputRequested) {
                        TerminalIO.input.add(inputField.getText());
                        TerminalIO.inputAdded = true;
                    } else {
                        Gamedata.handleInput(inputField.getText());
                    }
                    input.add(0, inputField.getText());
                    currInput = "";
                    inputIndex = -1;
                    inputField.setText("");
                }
            }
        });
        inputField.addMouseWheelListener(new TerminalGUI());
        inputField.addKeyListener(new TerminalGUI());

        inputField.setForeground(Color.WHITE);
        inputField.setBackground(Color.BLACK);

        prefixDisplay.setOpaque(true);
        prefixDisplay.setForeground(Color.WHITE);
        prefixDisplay.setBackground(Color.BLACK);

        UI.mainFrame.removeAllComponents();
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(userInputPanel, BorderLayout.SOUTH);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    public void updateFont() {
        Component[] components = centerPanel.getComponents();
        for (Component i : components) {
            i.setFont(new Font("Dialog", Font.PLAIN, fontSize));
        }
        components = userInputPanel.getComponents();
        for (Component i : components) {
            i.setFont(new Font("Dialog", Font.PLAIN, fontSize));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 38) { // Up arrow
            if (inputIndex == -1) {
                currInput = ((JTextField) userInputPanel.getComponent(0)).getText();
            }
            if (inputIndex < input.size() - 1) {
                inputIndex++;
            }
            if (!input.isEmpty()) {
                ((JTextField) userInputPanel.getComponent(0)).setText(input.get(inputIndex));
            }
        } else if (e.getKeyCode() == 40) { // Down arrow
            if (inputIndex <= 0) {
                ((JTextField) userInputPanel.getComponent(0)).setText(currInput);
                inputIndex = -1;
                return;
            } else {
                inputIndex--;
            }
            if (!input.isEmpty()) {
                ((JTextField) userInputPanel.getComponent(0)).setText(input.get(inputIndex));
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            if (e.getWheelRotation() < 0) {
                fontSize += fontSizeQuantum;
                updateFont();
            } else {
                if (fontSize > fontSizeQuantum) {
                    fontSize -= fontSizeQuantum;
                }
                updateFont();
            }
        }
    }
}
