package com.company.Morticia.UI;

import com.company.Morticia.UI.GUI.MainFrame;

import javax.swing.*;

public class UI {
    public static MainFrame mainFrame = new MainFrame();

    public static void start() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainFrame.show();
            }
        });
    }

    public static void tick() {

    }
}
