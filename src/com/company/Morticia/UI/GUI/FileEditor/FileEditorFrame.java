package com.company.Morticia.UI.GUI.FileEditor;

import com.company.Morticia.Computer.Commands.ProcessedText;
import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Computer.Filesystem.L_File;
import com.company.Morticia.Lua.LuaUtil;
import com.company.Morticia.UI.GUI.MainFrame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

public class FileEditorFrame implements ActionListener, MouseWheelListener, KeyListener {
    public JFrame frame;

    public JTextArea textArea;
    public JMenuBar menuBar;

    final UndoManager undo;
    public Document doc;

    public Computer computer;
    public L_File file;
    public boolean canWrite;

    public static int fontSize = 12;
    public final int fontSizeQuantum = 2;

    public JTextArea lines;
    public JScrollPane scrollPane;

    public FileEditorFrame(Computer computer, L_File file, boolean canWrite) {
        this.computer = computer;
        this.file = file;
        this.canWrite = canWrite;

        this.undo = new UndoManager();

        this.frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle(file.cName);
        frame.setSize(MainFrame.defaultWidth, MainFrame.defaultHeight);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                save();
                super.windowClosed(e);
            }
        });

        textArea = new JTextArea() {
            @Override
            public void setBorder(Border border) {
                // No border
            }
        };
        menuBar = new JMenuBar() {
            @Override
            public void setBorder(Border border) {
                // No border
            }
        };

        textArea.setFont(new Font("Dialog", Font.PLAIN, fontSize));

        textArea.addKeyListener(this);

        scrollPane = new JScrollPane() {
            @Override
            public void setBorder(Border border) {
                // No border
            }
        };

        scrollPane.addMouseWheelListener(this);

        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        JScrollBar horizontal = scrollPane.getHorizontalScrollBar();

        vertical.setPreferredSize(new Dimension(0, 0));
        horizontal.setPreferredSize(new Dimension(0, 0));

        lines = new JTextArea("1 ") {
            @Override
            public void setBorder(Border border) {
                // No border
            }
        };
        lines.setBackground(Color.BLACK);
        lines.setForeground(Color.GRAY);
        lines.setEditable(false);

        lines.setFont(new Font("Dialog", Font.PLAIN, fontSize));

        // Undo/redo listener
        doc = textArea.getDocument();
        doc.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
            }
        });
        doc.addDocumentListener(new DocumentListener() {
            public String getText() {
                int caretPosition = doc.getLength();
                Element root = doc.getDefaultRootElement();
                String lineSep = System.getProperty("line.separator");
                String text = "1 " + lineSep;
                for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
                    text += i + " " + lineSep;
                }
                return text;
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                lines.setText(getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lines.setText(getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lines.setText(getText());
            }
        });

        menuBar.addMouseWheelListener(this);
        menuBar.addKeyListener(this);;

        // Create menus
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu fontMenu = new JMenu("Font");

        // Create menu items
        JMenuItem saveMI = new JMenuItem("Save");
        JMenuItem clearMI = new JMenuItem("Clear");

        JMenuItem cutMI = new JMenuItem("Cut");
        JMenuItem copyMI = new JMenuItem("Copy");
        JMenuItem pasteMI = new JMenuItem("Paste");

        JMenuItem fontUpMI = new JMenuItem("Increase Font Size");
        JMenuItem fontDownMI = new JMenuItem("Decrease Font Size");

        // Add action listener
        saveMI.addActionListener(this);
        clearMI.addActionListener(this);
        cutMI.addActionListener(this);
        copyMI.addActionListener(this);
        pasteMI.addActionListener(this);
        fontUpMI.addActionListener(this);
        fontDownMI.addActionListener(this);

        // Add to menus
        fileMenu.add(saveMI);
        fileMenu.add(clearMI);

        editMenu.add(cutMI);
        editMenu.add(copyMI);
        editMenu.add(pasteMI);

        fontMenu.add(fontUpMI);
        fontMenu.add(fontDownMI);

        // Add to menubar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(fontMenu);

        // Set style
        frame.setBackground(Color.BLACK);

        menuBar.setBackground(Color.BLACK);

        fileMenu.setBackground(Color.BLACK);
        fileMenu.setForeground(Color.WHITE);

        editMenu.setBackground(Color.BLACK);
        editMenu.setForeground(Color.WHITE);

        fontMenu.setBackground(Color.BLACK);
        fontMenu.setForeground(Color.WHITE);

        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);

        scrollPane.setBackground(Color.BLACK);
        scrollPane.setForeground(Color.BLACK);

        scrollPane.getViewport().add(textArea);
        scrollPane.setRowHeaderView(lines);

        frame.setJMenuBar(menuBar);
        //frame.add(textArea);
        frame.add(scrollPane);

        file.editors.add(this);

        load();
    }

    public void show() {
        frame.setVisible(true);
    }

    public void save() {
        file.setContents(Arrays.asList(textArea.getText().split("\n")));
    }

    public void load() {
        StringBuilder buffer = new StringBuilder();
        List<String> data = file.content;
        if (data != null && !data.isEmpty()) {
            for (String i : data) {
                buffer.append(i + "\n");
            }
            buffer.deleteCharAt(buffer.lastIndexOf("\n"));
            textArea.setText(buffer.toString());
        } else {
            textArea.setText("");
            lines.setText("1 ");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "Save" -> save();
            case "Clear" -> this.textArea.setText("");
            case "Cut" -> this.textArea.cut();
            case "Copy" -> this.textArea.copy();
            case "Paste" -> this.textArea.paste();
            case "Increase Font Size" -> {
                fontSize += fontSizeQuantum;
                textArea.setFont(new Font("Dialog", Font.PLAIN, fontSize));
                lines.setFont(new Font("Dialog", Font.PLAIN, fontSize));
            }
            case "Decrease Font Size" -> {
                if (fontSize > fontSizeQuantum) {
                    fontSize -= fontSizeQuantum;
                }
                textArea.setFont(new Font("Dialog", Font.PLAIN, fontSize));
                lines.setFont(new Font("Dialog", Font.PLAIN, fontSize));
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            if (e.getWheelRotation() < 0) {
                fontSize += fontSizeQuantum;
                textArea.setFont(new Font("Dialog", Font.PLAIN, fontSize));
                lines.setFont(new Font("Dialog", Font.PLAIN, fontSize));
            } else {
                if (fontSize > fontSizeQuantum) {
                    fontSize -= fontSizeQuantum;
                }
                textArea.setFont(new Font("Dialog", Font.PLAIN, fontSize));
                lines.setFont(new Font("Dialog", Font.PLAIN, fontSize));
            }
        } else {

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
        if (e.isControlDown()) {
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
            } else if (e.getKeyCode() == 83) {
                save();
            }
        }
        if (e.isShiftDown()) {
            if (e.getKeyCode() == 121) { // shift + f10
                if (file.executable && file.canExecute(file.computer.currUser)) {
                    save();
                    LuaUtil.run(file.getPath(), new ProcessedText(""), file.computer);
                }
            }
        }
    }
}
