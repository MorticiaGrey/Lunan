package com.company.Morticia.Computer.User;

import com.company.Morticia.Computer.Computer;

import java.util.ArrayList;
import java.util.List;

public class UserGroup {
    public String groupName;
    public List<User> members;
    public Computer parent;

    public UserGroup(String groupName, Computer parent) {
        this.groupName = groupName;
        this.members = new ArrayList<>();
        this.parent = parent;
    }

    public void add(User user) {
        this.members.add(user);
    }

    public void remove(String userName) {
        members.removeIf(i -> i.uName.equals(userName));
    }

    public User get(String userName) {
        for (User i : this.members) {
            if (i.uName.equals(userName)) {
                return i;
            }
        }
        return null;
    }

    public boolean hasUser(User user) {
        for (User i : this.members) {
            if (i.equals(user)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.groupName;
    }
}
