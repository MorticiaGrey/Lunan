package com.company.Morticia.UI.GUI.Terminal;

import com.company.Morticia.Gamedata.Gamedata;
import com.company.Morticia.UI.UI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
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

    public static final UndoManager undo = new UndoManager();
    public static Document doc;

    public static JScrollPane scrollPane = new JScrollPane() {
        @Override
        public void setBorder(Border border) {
            // No border
        }
    };
    public static JScrollBar vertical;
    public static JScrollBar horizontal;

    /**
     * Starts the terminal interface
     */
    public static void start() {
        JFrame frame = UI.mainFrame.frame;

        centerPanel.setLayout(new GridBagLayout());
        userInputPanel.setLayout(new BorderLayout());

        centerPanel.setAlignmentX(0.0F);
        userInputPanel.setAlignmentX(0.0F);

        centerPanel.setAlignmentY(0.0F);
        userInputPanel.setAlignmentY(0.0F);

        centerPanel.setBackground(Color.BLACK);
        userInputPanel.setBackground(Color.BLACK);

        //centerPanel.setPreferredSize(new Dimension(1, 1));
        //userInputPanel.setPreferredSize(new Dimension(1, 1));

        TextWrappingJLabel outputDisplay = new TextWrappingJLabel("<html>");
        //outputDisplay.setHorizontalAlignment(SwingConstants.LEFT);
        //outputDisplay.setVerticalAlignment(SwingConstants.BOTTOM);
        outputDisplay.setBackground(Color.WHITE);
        outputDisplay.setForeground(Color.WHITE);
        //outputDisplay.setAlignmentX(0.0F);
        //outputDisplay.setPreferredSize(new Dimension(1, fontSize));

        scrollPane.addMouseWheelListener(new TerminalGUI());

        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setBackground(Color.BLACK);
        scrollPane.setForeground(Color.WHITE);

        vertical = scrollPane.getVerticalScrollBar();
        horizontal = scrollPane.getHorizontalScrollBar();

        vertical.setUnitIncrement(16);
        horizontal.setUnitIncrement(16);

        vertical.setPreferredSize(new Dimension(0, 0));
        horizontal.setPreferredSize(new Dimension(0, 0));

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = GridBagConstraints.REMAINDER;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.FIRST_LINE_START;
        c1.weightx = 1.0F;
        //c1.weighty = 1.0F;

        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 1;
        c2.gridwidth = GridBagConstraints.REMAINDER;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.FIRST_LINE_START;
        c2.weightx = 1.0F;
        c2.weighty = 1.0F;

        centerPanel.add(outputDisplay, c1);
        centerPanel.add(userInputPanel, c2);

        //boxPanel.add(centerPanel);
        //boxPanel.add(userInputPanel);

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
        doc = inputField.getDocument();
        doc.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
            }
        });

        inputField.setForeground(Color.WHITE);
        inputField.setBackground(Color.BLACK);

        prefixDisplay.setOpaque(true);
        prefixDisplay.setForeground(Color.WHITE);
        prefixDisplay.setBackground(Color.BLACK);

        updateFont();

        //UI.mainFrame.removeAllComponents();
        //frame.add(centerPanel, BorderLayout.NORTH);
        scrollPane.getViewport().add(centerPanel, BorderLayout.NORTH);
        frame.add(scrollPane);
        //frame.add(userInputPanel, BorderLayout.SOUTH);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    public static void updateFont() {
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
        } else if (e.isControlDown()) {
            if (e.getKeyCode() == 90) {
                try {
                    if (undo.canUndo()) {
                        undo.undo();
                    }
                } catch (CannotUndoException ignored) {}
            } else if (e.getKeyCode() == 89) {
                try {
                    if (undo.canRedo()) {
                        undo.redo();
                    }
                } catch (CannotRedoException ignored) {}
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

    public static void scrollToBottom() {
        vertical.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                vertical.removeAdjustmentListener(this);
            }
        });
    }
}
