package com.company.Morticia.Computer.Filesystem;

import com.company.Morticia.Computer.Commands.Command;
import com.company.Morticia.Computer.Computer;
import com.company.Morticia.UI.GUI.FileEditor.FileEditorFrame;
import com.company.Morticia.UI.GUI.Terminal.TerminalIO;
import com.company.Morticia.Util.DiscUtils.DiscFile;
import com.company.Morticia.Util.DiscUtils.DiscUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class L_File extends FilesystemComponent {
    public List<String> content;
    public DiscFile discFile;
    public boolean executable;
    public List<FileEditorFrame> editors;

    public L_File(Folder parent, String name, Computer computer) {
        super(parent, name, computer);
        this.content = new ArrayList<>();
        String path = computer.filesystem.path + getPath();
        this.discFile = new DiscFile(Paths.get(DiscUtils.makeObjectivePath(path)));
        this.executable = name.endsWith(".lua");
        this.editors = new ArrayList<>();
        if (parent.cName.equals("cmds") && parent.parent.isRoot) {
            computer.commands.add(new Command(this));
        }
    }

    public L_File(Folder parent, String name, Computer computer, String[] content) {
        super(parent, name, computer);
        this.content = new ArrayList<>(List.of(content));
        String path = computer.filesystem.path + getPath();
        this.discFile = new DiscFile(Paths.get(DiscUtils.makeObjectivePath(path)));
        executable = name.endsWith(".lua");
        this.editors = new ArrayList<>();
        if (parent.cName.equals("cmds") && parent.parent.isRoot) {
            computer.commands.add(new Command(this));
        }
    }

    public L_File(Folder parent, String name, Computer computer, List<String> content) {
        super(parent, name, computer);
        this.content = content;
        String path = computer.filesystem.path + getPath();
        this.discFile = new DiscFile(Paths.get(DiscUtils.makeObjectivePath(path)));
        executable = name.endsWith(".lua");
        this.editors = new ArrayList<>();
        if (parent.cName.equals("cmds") && parent.parent.isRoot) {
            computer.commands.add(new Command(this));
        }
    }

    public void setContents(List<String> newData) {
        content = new ArrayList<>(newData);
        onUpdateContents();
    }

    public void appendToContents(String newData) {
        content.add(newData);
        onUpdateContents();
    }

    public void appendToContents(int index, String newData) {
        content.add(index, newData);
        onUpdateContents();
    }

    public void onUpdateContents() {
        for (FileEditorFrame i : editors) {
            i.load();
        }
        save();
    }

    public String getHTMLName() {
        if (this.executable) {
            return TerminalIO.wrapInColor(cName, "37f702");
        } else {
            return TerminalIO.wrapInColor(cName, "03a00d");
        }
    }

    @Override
    public void save() {
        this.discFile = DiscUtils.writeFile(computer.filesystem.path + this.getPath(), this.content);
    }
}
