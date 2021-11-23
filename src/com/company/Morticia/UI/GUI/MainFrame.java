package com.company.Morticia.UI.GUI;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.UI.GUI.Terminal.TerminalGUI;
import com.company.Morticia.Util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame {
    public static final int defaultWidth = 1200;
    public static final int defaultHeight = 800;

    public JFrame frame;

    public MainFrame() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("Lunan");
        frame.setSize(defaultWidth, defaultHeight);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Util.terminate();
                super.windowClosed(e);
            }
        });

        frame.setBackground(Color.BLACK);
    }

    public void show() {
        TerminalGUI.start();
        frame.setVisible(true);
    }

    public void removeAllComponents() {
        frame.getContentPane().removeAll();
        frame.repaint();
    }
}
