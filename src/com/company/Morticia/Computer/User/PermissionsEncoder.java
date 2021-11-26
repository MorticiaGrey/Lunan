package com.company.Morticia.Computer.User;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Computer.Filesystem.Filesystem;
import com.company.Morticia.Computer.Filesystem.FilesystemComponent;
import com.company.Morticia.Computer.Filesystem.Folder;
import com.company.Morticia.Util.DiscUtils.DiscUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class PermissionsEncoder {
    public Computer computer;
    public Filesystem filesystem;
    public String savePath;
    public File saveFile;
    public String defaultPerms;

    public PermissionsEncoder(Filesystem filesystem, String defaultPerms) {
        this.computer = filesystem.parent;
        this.filesystem = filesystem;
        this.savePath = this.computer.path + "/filePermissions";
        this.saveFile = new File(this.savePath);
        this.defaultPerms = defaultPerms;

        DiscUtils.writeFile(this.savePath); // No idea why I have to put this but if I don't it won't recognize that the file exists even if it's clearly at that path
        List<String> data = DiscUtils.readFile(this.savePath);
        if (data != null && data.isEmpty()) {
            DiscUtils.writeFile(this.savePath, Collections.singletonList("default: " + defaultPerms));
        } else {
            String[] buffer = data.get(0).split(": ");
            if (buffer.length >= 2) {
                if (buffer[0].equals("default")) {
                    this.defaultPerms = buffer[1];
                }
            }
        }
    }

    public String formatString(FilesystemComponent in) {
        return in.getPath() + ": " + in.perms.permsString;
    }

    public FilesystemComponent loadString(String in) {
        String[] buffer = in.split(": ");
        if (buffer.length >= 2) {
            FilesystemComponent component = this.filesystem.getChild(buffer[0]);
            if (component != null) {
                component.setUserPermissions(buffer[1]);
            }
            return component;
        } else {
            return null;
        }
    }

    public void saveFolder(Folder folder) {
        for (FilesystemComponent i : folder.children) {
            DiscUtils.appendToFile(this.savePath, formatString(i));
        }
        for (Folder i : folder.folderChildren) {
            saveFolder(i);
        }
    }

    /**
     * Saves root's permissions and all the children of root's permissions
     */
    public void save() {
        /*List<String> data = DiscUtils.readFile(this.savePath);
        if (data != null) {
            if (!data.isEmpty()) {
                String[] buffer = data.get(0).split(": ");
                if () {

                }
            }
        }*/
        savePath = this.computer.path + "/filePermissions";
        DiscUtils.writeFile(this.savePath, Collections.singletonList("default: " + this.defaultPerms));
        saveFolder(filesystem.root);
    }

    public void loadDefault(Folder folder) {
        for (FilesystemComponent i : folder.children) {
            i.setUserPermissions(this.defaultPerms);
        }
        for (Folder i : folder.folderChildren) {
            loadDefault(i);
        }
    }

    /**
     * Loads root's permissions and all the children of root's permissions
     */
    public void load() {
        List<String> data = DiscUtils.readFile(this.savePath);
        if (data != null) {
            if (data.size() >= 1) {
                String[] buffer = data.get(0).split(": ");
                if (buffer.length >= 2) {
                    if (buffer[0].equals("default")) {
                        this.defaultPerms = buffer[1];
                    }
                }
            }
        }
        loadDefault(this.filesystem.root);
        List<String> savedList = DiscUtils.readFile(this.savePath);
        if (savedList != null) {
            for (String i : savedList) {
                loadString(i);
            }
        }
    }
}
