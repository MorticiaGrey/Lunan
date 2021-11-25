package com.company.Morticia.Computer.User;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Computer.Filesystem.Folder;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String uName;
    public String password;
    public Folder homeDir;
    public UserGroup group;
    public List<UserGroup> groups;
    public boolean sudoEnabled;

    public User(String uName, String password, Computer parent) {
        this.uName = uName;
        this.password = password;

        this.group = parent.getGroup(uName);
        if (this.group == null) {
            this.group = new UserGroup(uName, parent);
            parent.groups.add(group);
        }
        this.groups = new ArrayList<>();

        this.groups.add(parent.allUsers);
        this.groups.add(this.group);
        this.group.add(this);

        sudoEnabled = false;

        if (!this.uName.equals("root")) {
            String homeDirPath = "/home/" + uName;
            if (parent.filesystem.folderExists(homeDirPath)) {
                this.homeDir = parent.filesystem.getFolder(homeDirPath);
            } else {
                this.homeDir = parent.filesystem.writeFolder(homeDirPath);
            }
            this.homeDir.setUserPermissions("rw-/rw-/r--");
        } else {
            sudoEnabled = true;
        }
    }

    public boolean inGroup(String gName) {
        for (UserGroup i : groups) {
            if (i.groupName.equals(gName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User user)) {
            return false;
        } else {
            return user.homeDir.getPath().equals(this.homeDir.getPath());
        }
    }

    @Override
    public String toString() {
        return this.uName;
    }
}
