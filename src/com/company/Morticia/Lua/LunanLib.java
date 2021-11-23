package com.company.Morticia.Lua;

import com.company.Morticia.Computer.Commands.ProcessedText;
import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Computer.Filesystem.FilesystemComponent;
import com.company.Morticia.Computer.Filesystem.Folder;
import com.company.Morticia.Computer.Filesystem.L_File;
import com.company.Morticia.Computer.User.User;
import com.company.Morticia.Computer.User.UserGroup;
import com.company.Morticia.UI.GUI.FileEditor.FileEditorFrame;
import com.company.Morticia.UI.GUI.Terminal.TerminalIO;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.*;

/*
Planned Function List -
  - getCurrPath(): gets path of file being executed
  - setSelectedPath(): sets the path the player is operating in
  - makeFile(String path): Creates file at specified path, if it doesn't already exist. returns true/false if successful
  - readFile(String path): Reads contents of file at path if it can. returns true/false if successful
  - writeFile(String path, String contents): Writes contents to file at path, creates new file if none exists returns true/false if successful
  - makeFolder(String path): Creates folder at specified path if does not already exist. returns true/false if successful
  - getFolderChildren(String path): returns the names of all the children in a folder, returns nothing if folder does not exist
  - write(String output): writes text to console without newline
  - read(): reads next input from console
  - read(String option): reads input from console but will filter depending on option
  - read(int lower, int upper): reads input from console but only returns the characters from the specified bounds
  - time(): look into just implementing the os.time()/os.date() functions but in this library
  - execute(String cmd): Executes same way as command, looks for files in path then local folder if not objective path. If
  objective path (i.e. /home/bunny_boi/commands/test.lu) goes right to that file. Anything after file to execute is arg,
  separated by spaces in string cmd
 */

public class LunanLib extends TwoArgFunction {
    Computer computer;
    String path;

    public LunanLib(Computer computer, String path) {
        this.computer = computer;
        this.path = path;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("print", new l_print());
        library.set("write", new write());
        library.set("read", new read());
        library.set("clearTerminal", new clearTerminal());
        library.set("getCurrPath", new getCurrPath(this.path));
        library.set("getParentFolder", new getParentFolder(this.path));
        library.set("getSelectedPath", new getSelectedPath(this.computer));
        library.set("setSelectedPath", new setSelectedPath(this.computer));
        library.set("makeFile", new makeFile(this.computer));
        library.set("writeFile", new writeFile(this.computer));
        library.set("readFile", new readFile(this.computer));
        library.set("writeFolder", new writeFolder(this.computer));
        library.set("getFolderChildren", new getFolderChildren(this.computer));
        library.set("getFolderChildrenLong", new getFolderChildrenLong(this.computer));
        library.set("execute", new execute(this.computer));
        library.set("setPerms", new setPerms(this.computer));
        library.set("getPerms", new getPerms(this.computer));
        library.set("chmod", new chmod(this.computer));
        library.set("createUser", new createUser(this.computer));
        library.set("setUserPassword", new setUserPassword(this.computer));
        library.set("setUserName", new setUserName(this.computer));
        library.set("removeUser", new removeUser(this.computer));
        library.set("addUserGroup", new addUserGroup(this.computer));
        library.set("removeUserGroup", new removeUserGroup(this.computer));
        library.set("getUsers", new getUsers(this.computer));
        library.set("setCurrUser", new setCurrUser(this.computer));
        library.set("getFileGroup", new getFileGroup(this.computer));
        library.set("setFileGroup", new setFileGroup(this.computer));
        library.set("openFileEditor", new openFileEditor(this.computer));
        env.set("lunan", library);
        return library;
    }

    static class l_print extends OneArgFunction {
        public l_print() {}

        @Override
        public LuaValue call(LuaValue output) {
            TerminalIO.println(output.checkjstring());
            return null;
        }
    }

    static class write extends OneArgFunction {
        public write() {}

        @Override
        public LuaValue call(LuaValue output) {
            TerminalIO.print(output.checkjstring());
            return null;
        }
    }

    static class read extends OneArgFunction {
        public read() {}

        @Override
        public LuaValue call(LuaValue luaValue) {
            return LuaValue.valueOf(TerminalIO.nextLine(luaValue.checkjstring()));
        }
    }

    static class clearTerminal extends ZeroArgFunction {
        public clearTerminal() {}

        @Override
        public LuaValue call() {
            TerminalIO.clearTerminal();
            return null;
        }
    }

    static class getCurrPath extends ZeroArgFunction {
        public String path;

        public getCurrPath(String path) {
            this.path = path;
        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(this.path);
        }
    }

    static class getParentFolder extends ZeroArgFunction {
        public String path;

        public getParentFolder(String path) {
            this.path = path;
        }

        @Override
        public LuaValue call() {
            String[] buffer1 = this.path.split("/");
            StringBuilder buffer2 = new StringBuilder();
            for (String i : buffer1) {
                if (!i.endsWith(".lua")) {
                    buffer2.insert(0, "/" + i);
                }
            }
            return LuaValue.valueOf(buffer2.toString());
        }
    }

    static class getSelectedPath extends ZeroArgFunction {
        public Computer computer;

        public getSelectedPath(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call() {
            String path = computer.currFolder.getPath();
            if (path.endsWith("/")) {
                return LuaValue.valueOf(path);
            } else {
                return LuaValue.valueOf(path + "/");
            }
        }
    }

    static class setSelectedPath extends OneArgFunction {
        public Computer computer;

        public setSelectedPath(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            Folder folder = this.computer.filesystem.getFolder(luaValue.checkjstring());
            if (folder != null) {
                this.computer.currFolder = folder;
                return LuaValue.valueOf(true);
            } else {
                return LuaValue.valueOf(false);
            }
        }
    }

    static class makeFile extends OneArgFunction {
        public Computer computer;

        public makeFile(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            String path = luaValue.checkjstring();
            return LuaValue.valueOf(this.computer.filesystem.writeFile(path));
        }
    }

    static class writeFile extends TwoArgFunction {
        public Computer computer;

        public writeFile(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue, LuaValue luaValue1) {
            String path = luaValue.checkjstring();
            List<String> content = new ArrayList<>();

            for (int i = 0; i < luaValue1.checktable().length(); i++) {
                content.add(luaValue1.get(i).toString());
            }

            return LuaValue.valueOf(this.computer.filesystem.writeFile(path, content));
        }
    }

    static class readFile extends OneArgFunction {
        public Computer computer;

        public readFile(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            List<String> content = this.computer.filesystem.readFile(luaValue.checkjstring());
            LuaTable lua_content = new LuaTable();

            for (String i : content) {
                lua_content.add(LuaValue.valueOf(i));
            }

            return lua_content;
        }
    }

    static class writeFolder extends OneArgFunction {
        public Computer computer;

        public writeFolder(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            this.computer.filesystem.writeFolder(luaValue.checkjstring());
            return null;
        }
    }

    static class getFolderChildren extends TwoArgFunction {
        public Computer computer;

        public getFolderChildren(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue, LuaValue color) {
            Folder folder = this.computer.filesystem.getFolder(luaValue.checkjstring());
            if (folder == null) {
                return null;
            }
            List<FilesystemComponent> children = new ArrayList<>(folder.children);
            LuaTable lua_children = LuaTable.tableOf();

            if (color.checkboolean()) {
                for (FilesystemComponent i : children) {
                    lua_children.insert(lua_children.length(), LuaValue.valueOf(i.getHTMLName()));
                }
            } else {
                for (FilesystemComponent i : children) {
                    lua_children.insert(lua_children.length(), LuaValue.valueOf(i.cName));
                }
            }

            return lua_children;
        }
    }

    static class getFolderChildrenLong extends TwoArgFunction {
        public Computer computer;

        public getFolderChildrenLong(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue, LuaValue color) {
            Folder folder = this.computer.filesystem.getFolder(luaValue.checkjstring());
            if (folder == null) {
                return null;
            }
            List<FilesystemComponent> children = new ArrayList<>(folder.children);
            LuaTable lua_children = LuaTable.tableOf();

            if (color.checkboolean()) {
                for (FilesystemComponent i : children) {
                    lua_children.insert(lua_children.length(), LuaValue.valueOf(i.type + i.perms.permsString
                            + " " + i.owner.uName + " " + i.group.groupName + " " + i.getHTMLName()));
                }
            } else {
                for (FilesystemComponent i : children) {
                    lua_children.insert(lua_children.length(), LuaValue.valueOf(i.type + i.perms.permsString
                            + " " + i.owner.uName + " " + i.group.groupName + " " + i.cName));
                }
            }

            return lua_children;
        }
    }

    static class execute extends OneArgFunction {
        public Computer computer;

        public execute(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            this.computer.processCommand(new ProcessedText(luaValue.checkjstring()));
            return null;
        }
    }

    static class setPerms extends TwoArgFunction {
        public Computer computer;

        public setPerms(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue path, LuaValue perms) {
            FilesystemComponent child = this.computer.filesystem.getChild(path.checkjstring());
            User user = child.owner;
            if (user.equals(this.computer.currUser) || this.computer.currUser.equals(this.computer.rootUser)) {
                child.setUserPermissions(perms.checkjstring());
                return LuaValue.valueOf(true);
            } else {
                return LuaValue.valueOf(false);
            }
        }
    }

    static class getPerms extends OneArgFunction {
        public Computer computer;

        public getPerms(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue path) {
            return LuaValue.valueOf(this.computer.filesystem.getChild(path.checkjstring()).perms.permsString);
        }
    }

    static class chmod extends OneArgFunction {
        public Computer computer;

        public chmod(Computer computer) {
            this.computer = computer;
        }

        // 0 = what, 1 = who, 2 = which
        private List<String> parse(String in) {
            List<String> retValue = new ArrayList<>();
            String[] buffer;

            String[] regexArr = new String[]{"=", "+", "-"};
            for (String i : regexArr) {
                if (in.contains(i)) {
                    String pI = i;
                    if (i.equals("+")) {
                        pI = "\\" + i;
                    }
                    buffer = in.split(pI);
                    retValue.add(i);
                    if (buffer[0].isBlank()) {
                        retValue.add("a");
                        retValue.add(buffer[1]);
                    } else {
                        retValue.add(buffer[0]);
                        retValue.add(buffer[1]);
                    }
                    break;
                }
            }

            return retValue;
        }

        // I am aware this is a mess and I'm sorry, it's currently 9:15 am and I've been up since 4 am. if it works, it works
        @Override
        public LuaValue call(LuaValue luaValue) {
            try {
                String usageMessage = "usage: chmod [OPTIONS] [oga][-+=][rwx] [PATH]";
                ProcessedText in = new ProcessedText(luaValue.checkjstring());
                List<String> args = in.args;
                Map<Character, Integer> indexTable = new HashMap<>();
                indexTable.put('r', 0);
                indexTable.put('w', 1);
                indexTable.put('x', 2);
                indexTable.put('o', 0);
                indexTable.put('g', 3);
                indexTable.put('a', 6);

                if (args.size() >= 2) {
                    String path = args.get(1);
                    if (path.startsWith("/")) {
                        path = this.computer.currFolder.getPath() + args.get(1);
                    } else {
                        path = this.computer.currFolder.getPath() + "/" + args.get(1);
                    }
                    if (args.get(0).contains(",")) {
                        String[] buffer = args.get(0).split(",");
                        for (String i : buffer) {
                            List<String> parsedText = parse(i);
                            if (parsedText.isEmpty()) {
                                return null;
                            }
                            char[] perms;
                            if (parsedText.get(0).equals("=")) {
                                perms = new char[]{'-', '-', '-', '-', '-', '-', '-', '-', '-'};
                            } else if (parsedText.get(0).equals("+") || parsedText.get(0).equals("-")) {
                                perms = this.computer.filesystem.getChild(path).perms.permsString.replaceAll("/", "").toCharArray();
                            } else {
                                return null;
                            }
                            char[] whoLetters = parsedText.get(1).toCharArray();
                            char[] whichLetters = parsedText.get(2).toCharArray();
                            for (char who : whoLetters) {
                                for (char which : whichLetters) {
                                    if (parsedText.get(0).equals("-")) {
                                        perms[indexTable.get(who) + indexTable.get(which)] = '-';
                                    } else {
                                        perms[indexTable.get(who) + indexTable.get(which)] = which; // TODO: 11/22/21 copy paste
                                    }
                                }
                            }
                            StringBuilder finalPerms = new StringBuilder();
                            for (char character : perms) {
                                finalPerms.append(character);
                            }
                            finalPerms.insert(3, "/");
                            finalPerms.insert(7, "/");
                            this.computer.filesystem.getChild(path).setUserPermissions(finalPerms.toString());
                        }
                    } else {
                        List<String> parsedText = parse(args.get(0));
                        if (parsedText.isEmpty()) {
                            return null;
                        }
                        char[] perms;
                        if (parsedText.get(0).equals("=")) {
                            perms = new char[]{'-', '-', '-', '-', '-', '-', '-', '-', '-'};
                        } else if (parsedText.get(0).equals("+") || parsedText.get(0).equals("-")) {
                            perms = this.computer.filesystem.getChild(path).perms.permsString.replaceAll("/", "").toCharArray();
                        } else {
                            return null;
                        }
                        char[] whoLetters = parsedText.get(1).toCharArray();
                        char[] whichLetters = parsedText.get(2).toCharArray();
                        for (char who : whoLetters) {
                            for (char which : whichLetters) {
                                if (parsedText.get(0).equals("-")) {
                                    perms[indexTable.get(who) + indexTable.get(which)] = '-';
                                } else {
                                    perms[indexTable.get(who) + indexTable.get(which)] = which; // TODO: 11/22/21 copy paste
                                }
                            }
                        }
                        StringBuilder finalPerms = new StringBuilder();
                        for (char character : perms) {
                            finalPerms.append(character);
                        }
                        finalPerms.insert(3, "/");
                        finalPerms.insert(7, "/");
                        this.computer.filesystem.getChild(path).setUserPermissions(finalPerms.toString());
                    }
                } else {
                    TerminalIO.println(usageMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    static class createUser extends TwoArgFunction {
        public Computer computer;

        public createUser(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue name, LuaValue password) {
            User currUser = computer.currUser;
            if (!currUser.equals(computer.rootUser)) {
                return LuaValue.valueOf(false);
            }
            for (User i : computer.allUsers.members) {
                if (i.uName.equals(name.checkjstring())) {
                    return LuaValue.valueOf(false);
                }
            }
            User user = new User(name.checkjstring(), password.checkjstring(), computer);
            computer.groups.add(user.group);
            computer.allUsers.add(user);
            return LuaValue.valueOf(true);
        }
    }

    static class setUserPassword extends TwoArgFunction {
        public Computer computer;

        public setUserPassword(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue userName, LuaValue password) {
            User currUser = computer.currUser;
            if (!currUser.equals(computer.rootUser)) {
                return null;
            }
            computer.allUsers.get(userName.checkjstring()).password = password.checkjstring();
            return null;
        }
    }

    static class setUserName extends TwoArgFunction {
        public Computer computer;

        public setUserName(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue currName, LuaValue newName) {
            User currUser = computer.currUser;
            if (!currUser.equals(computer.rootUser)) {
                return null;
            }
            computer.allUsers.get(currName.checkjstring()).uName = newName.checkjstring();
            return null;
        }
    }

    static class removeUser extends OneArgFunction {
        public Computer computer;

        public removeUser(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue name) {
            User currUser = computer.currUser;
            if (!currUser.equals(computer.rootUser)) {
                return LuaValue.valueOf(false);
            }
            User user = computer.allUsers.get(name.checkjstring());
            if (user == null) {
                return LuaValue.valueOf(false);
            }
            computer.allUsers.remove(name.checkjstring());
            computer.groups.remove(user.group);
            return LuaValue.valueOf(true);
        }
    }

    // Adds user to group
    static class addUserGroup extends TwoArgFunction {
        public Computer computer;

        public addUserGroup(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue userName, LuaValue groupName) {
            User currUser = computer.currUser;
            if (!currUser.equals(computer.rootUser) || computer.allUsers.get(userName.checkjstring()) == null) {
                return null;
            }
            User user = computer.allUsers.get(userName.checkjstring());
            for (UserGroup i : computer.groups) {
                if (i.groupName.equals(groupName.checkjstring())) {
                    i.add(user);
                    user.groups.add(i);
                }
            }
            return null;
        }
    }

    static class removeUserGroup extends TwoArgFunction {
        public Computer computer;

        public removeUserGroup(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue userName, LuaValue groupName) {
            User currUser = computer.currUser;
            if (!currUser.equals(computer.rootUser) || computer.allUsers.get(userName.checkjstring()) == null) {
                return null;
            }
            User user = computer.allUsers.get(userName.checkjstring());
            for (UserGroup i : computer.groups) {
                if (i.groupName.equals(groupName.checkjstring())) {
                    i.remove(userName.checkjstring());
                    user.groups.remove(i);
                }
            }
            return null;
        }
    }

    static class getUsers extends ZeroArgFunction {
        public Computer computer;

        public getUsers(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call() {
            LuaTable table = LuaValue.tableOf();
            List<User> users = computer.allUsers.members;

            for (User i : users) {
                table.insert(table.length(), LuaValue.valueOf(i.uName));
            }

            return table;
        }
    }

    static class setCurrUser extends TwoArgFunction {
        public Computer computer;

        public setCurrUser(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue username, LuaValue password) {
            User user = computer.allUsers.get(username.checkjstring());
            if (user == null) {
                return LuaValue.valueOf(false);
            }
            if (user.password.equals(password.checkjstring())) {
                computer.currUser = user;
                return LuaValue.valueOf(true);
            }
            return LuaValue.valueOf(false);
        }
    }

    // Get group of a file
    static class getFileGroup extends OneArgFunction {
        public Computer computer;

        public getFileGroup(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            FilesystemComponent child = computer.filesystem.getChild(luaValue.toString());
            if (child == null) {
                return null;
            }
            return LuaValue.valueOf(child.group.groupName);
        }
    }

    static class setFileGroup extends TwoArgFunction {
        public Computer computer;

        public setFileGroup(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue path, LuaValue groupName) {
            FilesystemComponent comp = computer.filesystem.getChild(path.checkjstring());
            if (comp == null) {
                return LuaValue.valueOf(false);
            }
            UserGroup group = null;
            for (UserGroup i : computer.groups) {
                if (i.groupName.equals(groupName.checkjstring())) {
                    group = i;
                    break;
                }
            }
            if (group == null) {
                return LuaValue.valueOf(false);
            }
            comp.group = group;
            return LuaValue.valueOf(true);
        }
    }

    static class openFileEditor extends OneArgFunction {
        public Computer computer;

        public openFileEditor(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            L_File file = this.computer.filesystem.getFile(luaValue.checkjstring());
            if (file != null) {
                new FileEditorFrame(this.computer, file, file.canWrite(this.computer.currUser)).show();
                return LuaValue.valueOf(true);
            } else {
                return LuaValue.valueOf(false);
            }
        }
    }
}
