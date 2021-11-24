package com.company.Morticia.Computer.Filesystem;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Computer.User.User;
import com.company.Morticia.Computer.User.UserGroup;
import com.company.Morticia.Computer.User.UserPermissions;
import com.company.Morticia.UI.GUI.Terminal.TerminalIO;

public class FilesystemComponent {
    public Folder parent; // It case of folders null if root
    public String cName;
    public Computer computer;
    public User owner;
    public UserGroup group;
    public UserPermissions perms;
    public String type;

    public FilesystemComponent(Folder parent, String cName, Computer computer) {
        this.parent = parent;
        this.cName = cName;
        this.computer = computer;
        this.owner = computer.currUser;
        this.group = computer.currUser.group;
        if (this.parent != null) {
            this.perms = parent.perms;
        } else {
            this.perms = new UserPermissions("rwx/rwx/r--");
        }
        this.type = "-";
    }

    /**
     * Gets the string path to this object
     *
     * @return The path through the filesystem to this object
     */
    public String getPath() {
        if (this.parent != null) {
            StringBuilder buffer = new StringBuilder(this.cName);
            Folder folder = this.parent;
            while (!folder.isRoot) {
                buffer.insert(0, folder.cName + "/");
                folder = folder.parent;
            }
            buffer.insert(0, "/");
            return buffer.toString();
        } else {
            return "/";
        }
    }

    public void setUserPermissions(String perms) {
        this.perms = new UserPermissions(perms);
    }

    public boolean canRead(User user) {
        if (user.equals(this.computer.rootUser)) {
            return true;
        } else if (user.equals(this.owner)) {
            return this.perms.ownerRead;
        } else if (user.groups.contains(this.group)) {
            return this.perms.groupRead;
        } else {
            return this.perms.allRead;
        }
    }

    public boolean canWrite(User user) {
        if (user.equals(this.computer.rootUser)) {
            return true;
        } else if (user.equals(this.owner)) {
            return this.perms.ownerWrite;
        } else if (user.groups.contains(this.group)) {
            return this.perms.groupWrite;
        } else {
            return this.perms.allWrite;
        }
    }

    public boolean canExecute(User user) {
        if (user.equals(this.computer.rootUser)) {
            return true;
        } else if (user.equals(this.owner)) {
            return this.perms.ownerExecute;
        } else if (user.groups.contains(this.group)) {
            return this.perms.groupExecute;
        } else {
            return this.perms.allExecute;
        }
    }

    public String getHTMLName() {
        return TerminalIO.wrapInColor(cName, "FFFFFF");
    }

    public void delete() {
        parent.remove(cName);
    }

    public void save() {

    }
}
