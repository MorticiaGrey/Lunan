package com.company.Morticia.Computer.Filesystem;

import com.company.Morticia.Computer.Commands.Command;
import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Computer.User.PermissionsEncoder;
import com.company.Morticia.Util.Constants;
import com.company.Morticia.Util.DiscUtils.DiscUtils;

import java.util.Arrays;
import java.util.List;

public class Filesystem {
    public Computer parent;
    public String path;
    public Folder root;
    public PermissionsEncoder permsEncoder;

    public Filesystem(Computer parent) {
        this.parent = parent;
        this.path = this.parent.path + "/filesystem";
        this.root = new Folder(null, "", this.parent);
        Folder rootHomeDir = new Folder(this.root, "root", parent);
        rootHomeDir.setUserPermissions("rwx/rwx/r--");
        this.root.children.add(rootHomeDir);
        this.root.folderChildren.add(rootHomeDir);
        this.parent.rootUser.homeDir = rootHomeDir;
        this.parent.currFolder = this.root;
        this.permsEncoder = new PermissionsEncoder(this, "rwx/rwx/rw-");

        DiscUtils.writeFolder(this.path);
    }

    /**
     * Gets the folder from a path starting at root
     *
     * @param path Path to folder object
     * @return Folder object
     */
    public Folder getFolder(String path) {
        if (path.equals("/")) {
            return this.root;
        }
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String[] pathSegments = path.split("/");
        Folder folder = root;
        for (String pathSegment : pathSegments) {
            Folder child = folder.getFolder(pathSegment);
            if (child != null) {
                folder = child;
            } else {
                return null;
            }
        }
        return folder;
    }

    /**
     * Gets a file specified by the path starting at root
     *
     * @param path Path to requested file
     * @return File requested
     */
    public L_File getFile(String path) {
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String[] pathSegments = path.split("/");
        Folder folder = root;
        int i;
        for (i = 0; i < pathSegments.length - 1; i++) {
            String pathSegment = pathSegments[i];
            Folder child = folder.getFolder(pathSegment);
            if (child != null) {
                folder = child;
            } else {
                return null;
            }
        }
        return folder.getFile(pathSegments[pathSegments.length - 1]);
    }

    /**
     * Gets a FilesystemComponent at specified location
     *
     * @param path Path to component
     * @return Component, null if not found
     */
    public FilesystemComponent getChild(String path) {
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String[] pathSegments = path.split("/");
        Folder folder = root;
        int i;
        for (i = 0; i < pathSegments.length - 2; i++) {
            String pathSegment = pathSegments[i];
            Folder child = folder.getFolder(pathSegment);
            if (child != null) {
                folder = child;
            } else {
                return null;
            }
        }
        return folder.getChild(pathSegments[pathSegments.length - 1]);
    }

    /**
     * Returns the contents of the file specified by the path
     *
     * @param path Path to file being read
     * @return Contents of file specified by path
     */
    public List<String> readFile(String path) {
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String[] pathSegments = path.split("/");
        Folder folder = root;
        for (int i = 0; i < pathSegments.length - 2; i++) {
            FilesystemComponent child = folder.getChild(pathSegments[i]);
            if (child != null) {
                folder = (Folder) child;
            } else {
                return null;
            }
        }
        return ((L_File)folder.getChild(pathSegments[pathSegments.length - 1])).content;
    }

    /**
     * Creates empty file at specified path
     *
     * @param path Path to new file
     * @return Whether or not operation was successful
     */
    public boolean writeFile(String path) {
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String[] pathSegments = path.split("/");
        Folder folder = root;
        for (int i = 0; i < pathSegments.length - 1; i++) {
            Folder child = (Folder) folder.getChild(pathSegments[i]);
            if (child != null) {
                folder = child;
            } else {
                return false;
            }
        }
        folder.add(new L_File(folder, pathSegments[pathSegments.length - 1], this.parent));
        return true;
    }

    /**
     * Creates file at specified path with specified contents
     *
     * @param path Path to new file
     * @param content Content of new file
     * @return Whether or not operation was successful
     */
    public boolean writeFile(String path, List<String> content) {
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String[] pathSegments = path.split("/");
        Folder folder = root;
        for (int i = 0; i < pathSegments.length - 2; i++) {
            Folder child = (Folder) folder.getChild(pathSegments[i]);
            if (child != null) {
                folder = child;
            } else {
                return false;
            }
        }
        folder.add(new L_File(folder, pathSegments[pathSegments.length - 1], this.parent, content));
        return true;
    }

    /**
     * Returns a list of the names of all the children of a folder specified by the path
     *
     * @param path Path to folder
     * @return List of names of children
     */
    public List<String> getFolderChildren(String path) {
        if (path.equals("/")) {
            return this.root.childrenNames();
        }
        Folder folder = getFolder(path);
        if (folder != null) {
            return folder.childrenNames();
        } else {
            return null;
        }
    }

    /**
     * Returns whether folder at specified path exists
     *
     * @param path Path to folder being inquired after
     * @return Whether folder exists
     */
    public boolean folderExists(String path) {
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String[] pathSegments = path.split("/");
        Folder folder = root;
        for (int i = 0; i < pathSegments.length - 2; i++) {
            Folder child = (Folder) folder.getChild(pathSegments[i]);
            if (child != null) {
                folder = child;
            } else {
                return false;
            }
        }
        return folder.childrenNames().contains(pathSegments[pathSegments.length - 1]);
    }

    /**
     * Creates a folder at specified path. Will write all steps in path if they do not exist
     *
     * @param path Path to new folder
     * @return Folder that was created
     */
    public Folder writeFolder(String path) {
        if (folderExists(path)) {
            return getFolder(path);
        }
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String[] pathSegments = path.split("/");
        Folder folder = root;
        for (String pathSegment : pathSegments) {
            Folder child = folder.getFolder(pathSegment);
            if (child != null) {
                folder = child;
            } else {
                Folder newFolder = new Folder(folder, pathSegment, this.parent);
                folder.add(newFolder);
                folder = newFolder;
            }
        }
        return folder;
    }

    /**
     * Saves filesystem to disc
     */
    public void save() {
        root.saveChildren();
        permsEncoder.save();
    }

    /**
     * Loads filesystem from disc
     */
    public void load() {
        root.loadChildren();
        permsEncoder.load();
    }

    public void initDefault() {
        String bufferPath = this.path;
        String bufferSavePath = this.permsEncoder.savePath;

        this.path = Constants.defaultFileSystemPath;
        this.permsEncoder.savePath = Constants.defaultFileSystemPermsSavePath;
        load();

        this.path = bufferPath;
        this.permsEncoder.savePath = bufferSavePath;
        save();
    }

    /**
     * Adds all executables in the /cmds folder to the list of executable commands
     */
    public void initDefaultCommands() {
        if (folderExists("/cmds")) {
            Folder cmds = (Folder) this.root.getChild("cmds");
            for (FilesystemComponent i : cmds.children) {
                if (i instanceof L_File file) {
                    if (file.executable) {
                        this.parent.commands.add(new Command(file));
                    }
                }
            }
        }
    }
}