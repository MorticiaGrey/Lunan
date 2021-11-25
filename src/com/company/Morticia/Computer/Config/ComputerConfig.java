package com.company.Morticia.Computer.Config;

import com.company.Morticia.UI.GUI.FileEditor.FileEditorFrame;
import com.company.Morticia.UI.GUI.Terminal.TerminalGUI;
import com.company.Morticia.Util.Constants;
import com.company.Morticia.Util.DiscUtils.DiscUtils;

import java.util.ArrayList;
import java.util.List;

public class ComputerConfig {
    public String path;

    public String compName;
    public boolean acceptPlayerControl;

    /**
     * Takes a path to a file and interprets the text in it to convert it to easily accessible variables
     *
     * @param path Path to file with text
     */
    public ComputerConfig(String path) {
        List<String> text = DiscUtils.readFile(path);
        if (text == null) {
            DiscUtils.writeFile(path);
            text = new ArrayList<>();
        }
        for (String i : text) {
            String[] line = i.split(" ");
            if (line.length > 2) {
                if (!line[0].isBlank() && !line[1].isBlank()) {
                    if (line[0].startsWith("name:")) {
                        this.compName = line[1];
                    } else if (line[0].startsWith("acceptPlayerControl:")) {
                        this.acceptPlayerControl = Boolean.parseBoolean(line[1]);
                    } else if (line[0].startsWith("terminalFontSize: ")) {
                        try {
                            TerminalGUI.fontSize = Integer.parseInt(line[1]);
                        } catch (Exception ignored) {
                            TerminalGUI.fontSize = 12;
                        }
                        TerminalGUI.updateFont();
                    } else if (line[0].startsWith("fileEditorFontSize: ")) {
                        try {
                            FileEditorFrame.fontSize = Integer.parseInt(line[1]);
                        } catch (Exception ignored) {
                            FileEditorFrame.fontSize = 12;
                        }
                    }
                }
            }
        }
    }

    public ComputerConfig(String compName, boolean acceptPlayerControl) {
        this.path = Constants.computersPath + "/" + compName + "/config";
        this.compName = compName;
        this.acceptPlayerControl = acceptPlayerControl;
        DiscUtils.writeFile(this.path, new String[]{"name: " + this.compName, "acceptPlayerControl: " + this.acceptPlayerControl,
                "terminalFontSize: " + TerminalGUI.fontSize, "fileEditorFontSize: " + FileEditorFrame.fontSize});
    }

    public void save() {
        DiscUtils.writeFile(this.path, new String[]{"name: " + this.compName, "acceptPlayerControl: " + this.acceptPlayerControl,
                "terminalFontSize: " + TerminalGUI.fontSize, "fileEditorFontSize: " + FileEditorFrame.fontSize});
    }
}
