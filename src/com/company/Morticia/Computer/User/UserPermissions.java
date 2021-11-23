package com.company.Morticia.Computer.User;

public class UserPermissions {
    public String permsString;

    public boolean ownerRead;
    public boolean ownerWrite;
    public boolean ownerExecute;

    public boolean groupRead;
    public boolean groupWrite;
    public boolean groupExecute;

    public boolean allRead;
    public boolean allWrite;
    public boolean allExecute;

    public UserPermissions(String perms) {
        this.permsString = perms;
        String[] permsBuffer = perms.split("/");
        setUserPermissions(permsBuffer[0], permsBuffer[1], permsBuffer[2]);
    }

    public UserPermissions(String ownerPerms, String groupPerms, String allPerms) {
        this.permsString = ownerPerms + "/" + groupPerms + "/" + allPerms;
        setUserPermissions(ownerPerms, groupPerms, allPerms);
    }

    public void setUserPermissions(String ownerPerms, String groupPerms, String allPerms) {
        char[] perms = ownerPerms.toCharArray();
        ownerRead = perms[0] == 'r';
        ownerWrite = perms[1] == 'w';
        ownerExecute = perms[2] == 'x';

        perms = groupPerms.toCharArray();
        groupRead = perms[0] == 'r';
        groupWrite = perms[1] == 'w';
        groupExecute = perms[2] == 'x';

        perms = allPerms.toCharArray();
        allRead = perms[0] == 'r';
        allWrite = perms[1] == 'w';
        allExecute = perms[2] == 'x';
    }
}
