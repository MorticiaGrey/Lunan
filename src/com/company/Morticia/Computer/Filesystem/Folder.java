package com.company.Morticia.Computer.Filesystem;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.UI.GUI.Terminal.TerminalIO;
import com.company.Morticia.Util.DiscUtils.DiscUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Folder extends FilesystemComponent {
    public List<FilesystemComponent> children;
    public List<Folder> folderChildren;
    public List<L_File> fileChildren;
    public boolean isRoot;

    public Folder(Folder parent, String name, Computer computer) {
        super(parent, name, computer);
        this.isRoot = this.parent == null;
        if (this.isRoot) {
            this.cName = "/";
        }
        this.folderChildren = new ArrayList<>();
        this.fileChildren = new ArrayList<>();
        this.children = new ArrayList<>();
        this.type = "d";
        if (this.computer.filesystem != null) {
            save();
        }
    }

    /**
     * Sets the user permissions of this folder and all children below this folder in the filesystem
     *
     * @param perms Permissions to be set to
     */
    @Override
    public void setUserPermissions(String perms) {
        super.setUserPermissions(perms);
        updateChildrenPermissions();
    }

    /**
     * Updates the permissions of all children below this in the filesystem
     */
    public void updateChildrenPermissions() {
        for (FilesystemComponent i : this.children) {
            i.setUserPermissions(this.perms.permsString);
        }
        for (Folder i : this.folderChildren) {
            i.updateChildrenPermissions();
        }
    }

    public void add(FilesystemComponent[] children) {
        if (!this.canWrite(this.computer.currUser)) {
            return;
        }
        for (FilesystemComponent i : this.children) {
            for (FilesystemComponent j : children) {
                if (i.cName.equals(j.cName)) {
                    return;
                }
            }
        }
        this.children.addAll(Arrays.asList(children));
        for (FilesystemComponent i : children) {
            if (i instanceof Folder) {
                folderChildren.add((Folder) i);
            } else if (i instanceof L_File) {
                fileChildren.add((L_File) i);
            }
        }
    }

    public void add(FilesystemComponent child) {
        if (!this.canWrite(this.computer.currUser)) {
            return;
        }
        for (FilesystemComponent i : children) {
            if (i.cName.equals(child.cName)) {
                return;
            }
        }
        this.children.add(child);
        if (child instanceof Folder) {
            folderChildren.add((Folder) child);
        } else if (child instanceof L_File) {
            fileChildren.add((L_File) child);
        }
    }

    public boolean remove(String name) {
        if (!this.canWrite(this.computer.currUser)) {
            return false;
        }
        for (FilesystemComponent i : children) {
            if (i.cName.equals(name)) {
                children.remove(i);
                if (i instanceof Folder) {
                    folderChildren.remove((Folder) i);
                } else if (i instanceof L_File) {
                    fileChildren.remove((L_File) i);
                }
                return true;
            }
        }
        return false;
    }

    public boolean remove(FilesystemComponent child) {
        if (!this.canWrite(this.computer.currUser)) {
            return false;
        }
        if (child instanceof Folder) {
            folderChildren.remove((Folder) child);
        } else if (child instanceof L_File) {
            fileChildren.remove((L_File) child);
        }
        return this.children.remove(child);
    }

    public List<String> childrenNames() {
        if (!this.canRead(this.computer.currUser)) {
            return new ArrayList<>();
        }
        List<String> buffer = new ArrayList<>();
        for (FilesystemComponent i : this.children) {
            buffer.add(i.cName);
        }
        return buffer;
    }

    public FilesystemComponent getChild(String name) {
        if (!this.canRead(this.computer.currUser)) {
            return null;
        }
        if (name.equals("..") && !this.isRoot) {
            return this.parent;
        }
        for (FilesystemComponent i : this.children) {
            if (i.cName.equals(name)) {
                return i;
            }
        }
        return null;
    }

    public Folder getFolder(String name) {
        if (!this.canRead(this.computer.currUser)) {
            return null;
        }
        if (name.equals("..") && !this.isRoot) {
            return this.parent;
        }
        for (Folder i : folderChildren) {
            if (i.cName.equals(name)) {
                return i;
            }
        }
        return null;
    }

    public L_File getFile(String name) {
        if (!this.canRead(this.computer.currUser)) {
            return null;
        }
        for (L_File i : fileChildren) {
            if (i.cName.equals(name)) {
                return i;
            }
        }
        return null;
    }

    @Override
    public String getHTMLName() {
        return TerminalIO.wrapInColor(cName, "7681fc");
    }

    /**
     * Saves all the children of this folder to disc and recursively calls this function
     * on all child folders so that every filesystem object under this is saved
     */
    public void saveChildren() {
        for (FilesystemComponent i : children) {
            i.save();
        }
        for (Folder i : folderChildren) {
            i.saveChildren();
        }
    }

    @Override
    public void save() {
        DiscUtils.writeFolder(this.computer.filesystem.path + this.getPath());
    }

    /**
     * Loads children from disc, function recursively called to get all children below
     * provided path
     */
    public void loadChildren() {
        File[] f_children = DiscUtils.getDirChildren(this.computer.filesystem.path + this.getPath());
        if (f_children == null) {
            return;
        }
        for (File f : f_children) {
            if (f.isDirectory()) {
                Folder folder = new Folder(this, f.getName(), computer);
                if (this.getChild(folder.cName) == null) {
                    this.add(folder);
                }
            } else {
                L_File file = new L_File(this, f.getName(), computer, DiscUtils.readFile(f));
                if (this.getChild(file.cName) == null) {
                    this.add(file);
                }
            }
        }
        for (Folder i : folderChildren) {
            i.loadChildren();
        }
    }
}
