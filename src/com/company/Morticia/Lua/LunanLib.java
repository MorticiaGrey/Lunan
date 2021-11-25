package com.company.Morticia.Lua;

import com.company.Morticia.Computer.Commands.Command;
import com.company.Morticia.Computer.Commands.ProcessedText;
import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Computer.Filesystem.FilesystemComponent;
import com.company.Morticia.Computer.Filesystem.Folder;
import com.company.Morticia.Computer.Filesystem.L_File;
import com.company.Morticia.Computer.User.User;
import com.company.Morticia.Computer.User.UserGroup;
import com.company.Morticia.Events.Event;
import com.company.Morticia.UI.GUI.FileEditor.FileEditorFrame;
import com.company.Morticia.UI.GUI.Terminal.TerminalIO;
import com.company.Morticia.Util.Constants;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.*;

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
  - consider adding way for player to add globals that other scripts are able to access, work would have to be done
  to insure they aren't overriding A) globals I set it in place and B) without perms to
  - consider graphical interface, maybe in separate labeled library, that interprets html and displays it.
  Input would have to be handled differently, probably it would trigger events.
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
        library.set("removeChild", new removeChild(this.computer));
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
        library.set("setPrimaryUserGroup", new setPrimaryUserGroup(this.computer));
        library.set("userExists", new userExists(this.computer));
        library.set("getUsers", new getUsers(this.computer));
        library.set("getGroups", new getGroups(this.computer));
        library.set("removeGroup", new removeGroup(this.computer));
        library.set("addGroup", new addGroup(this.computer));
        library.set("getCurrUser", new getCurrUser(this.computer));
        library.set("setCurrUser", new setCurrUser(this.computer));
        library.set("getFileGroup", new getFileGroup(this.computer));
        library.set("setFileGroup", new setFileGroup(this.computer));
        library.set("openFileEditor", new openFileEditor(this.computer));
        library.set("triggerEvent", new triggerEvent(this.computer));
        library.set("sendPacket", new sendPacket(this.computer));
        library.set("executeScript", new executeScript(this.computer));
        library.set("registerCommand", new registerCommand(this.computer));
        env.set("lunan", library);
        return library;
    }

    static class l_print extends OneArgFunction {
        public l_print() {}

        @Override
        public LuaValue call(LuaValue output) {
            if (output.isboolean()) {
                TerminalIO.println(output.checkboolean());
            } else if (output.isint()) {
                TerminalIO.println(output.checkint());
            } else if (output.istable()) {
                LuaTable table = (LuaTable) output;
                StringBuilder buffer = new StringBuilder();
                for (int i = 0; i < table.length(); i++) {
                    buffer.append(table.get(i));
                }
                TerminalIO.println(buffer.toString());
            } else if (output.isstring()){
                TerminalIO.println(output.checkjstring());
            }
            return null;
        }
    }

    static class write extends OneArgFunction {
        public write() {}

        @Override
        public LuaValue call(LuaValue output) {
            if (output.isboolean()) {
                TerminalIO.print(output.checkboolean());
            } else if (output.isint()) {
                TerminalIO.print(output.checkint());
            } else if (output.istable()) {
                LuaTable table = (LuaTable) output;
                StringBuilder buffer = new StringBuilder();
                for (int i = 0; i < table.length(); i++) {
                    buffer.append(table.get(i));
                }
                TerminalIO.print(buffer.toString());
            } else if (output.isstring()){
                TerminalIO.print(output.checkjstring());
            }
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

    static class readFile extends TwoArgFunction {
        public Computer computer;

        public readFile(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue, LuaValue htmlSafe) {
            try {
                L_File file = computer.filesystem.getFile(luaValue.checkjstring());
                if (file == null) {
                    return null;
                }
                List<String> content = file.content;
                LuaTable lua_content = new LuaTable();

                if (htmlSafe.checkboolean()) {
                    for (String i : content) {
                        lua_content.insert(0, LuaValue.valueOf(i.replaceAll(" ", Constants.htmlSpace)));
                    }
                } else {
                    for (String i : content) {
                        lua_content.insert(0, LuaValue.valueOf(i));
                    }
                }

                return lua_content;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    static class writeFolder extends OneArgFunction {
        public Computer computer;

        public writeFolder(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            return LuaValue.valueOf(computer.filesystem.writeFolder(luaValue.checkjstring()) == null);
        }
    }

    // Removes a file or folder at path specified
    static class removeChild extends OneArgFunction {
        public Computer computer;

        public removeChild(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue path) {
            return LuaValue.valueOf(computer.filesystem.deleteChild(path.checkjstring()));
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
                    if (i.canRead(computer.currUser)) {
                        lua_children.insert(lua_children.length(), LuaValue.valueOf(i.getHTMLName()));
                    }
                }
            } else {
                for (FilesystemComponent i : children) {
                    if (i.canRead(computer.currUser)) {
                        lua_children.insert(lua_children.length(), LuaValue.valueOf(i.cName));
                    }
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
                    if (i.canRead(computer.currUser)) {
                        lua_children.insert(lua_children.length(), LuaValue.valueOf(i.type + i.perms.permsString
                                + " " + i.owner.uName + " " + i.group.groupName + " " + i.getHTMLName()));
                    }
                }
            } else {
                for (FilesystemComponent i : children) {
                    if (i.canRead(computer.currUser)) {
                        lua_children.insert(lua_children.length(), LuaValue.valueOf(i.type + i.perms.permsString
                                + " " + i.owner.uName + " " + i.group.groupName + " " + i.cName));
                    }
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
            if (user.equals(this.computer.currUser) || !computer.hasRootPerms(computer.currUser)) {
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
                    } else {
                        retValue.add(buffer[0]);
                    }
                    retValue.add(buffer[1]);
                    break;
                }
            }

            return retValue;
        }

        // I am aware this is a mess and I'm sorry, it's currently 9:15 am and I've been up since 4 am. if it works, it works
        @Override
        public LuaValue call(LuaValue luaValue) {
            if (!computer.hasRootPerms(computer.currUser)) {
                TerminalIO.println("error: insufficient permissions");
                return LuaValue.valueOf(false);
            }
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

            return LuaValue.valueOf(true);
        }
    }

    static class createUser extends TwoArgFunction {
        public Computer computer;

        public createUser(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue name, LuaValue password) {
            if (!computer.hasRootPerms(computer.currUser)) {
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
            if (!computer.hasRootPerms(computer.currUser)) {
                return LuaValue.valueOf(false);
            }
            computer.allUsers.get(userName.checkjstring()).password = password.checkjstring();
            return LuaValue.valueOf(true);
        }
    }

    static class setUserName extends TwoArgFunction {
        public Computer computer;

        public setUserName(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue currName, LuaValue newName) {
            if (!computer.hasRootPerms(computer.currUser)) {
                return LuaValue.valueOf(false);
            }
            computer.allUsers.get(currName.checkjstring()).uName = newName.checkjstring();
            return LuaValue.valueOf(true);
        }
    }

    static class removeUser extends OneArgFunction {
        public Computer computer;

        public removeUser(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue name) {
            if (!computer.hasRootPerms(computer.currUser)) {
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
            if (!computer.hasRootPerms(computer.currUser) || computer.allUsers.get(userName.checkjstring()) == null) {
                return LuaValue.valueOf(false);
            }
            User user = computer.allUsers.get(userName.checkjstring());
            String gName = groupName.checkjstring();
            if (gName.contains(",")) {
                String[] groupNames = gName.split(",");
                for (String i : groupNames) {
                    for (UserGroup j : computer.groups) {
                        if (j.groupName.equals(i)) {
                            j.add(user);
                            user.groups.add(j);
                        }
                    }
                }
                return LuaValue.valueOf(true);
            } else {
                for (UserGroup i : computer.groups) {
                    if (i.groupName.equals(groupName.checkjstring())) {
                        i.add(user);
                        user.groups.add(i);
                        return LuaValue.valueOf(true);
                    }
                }
                return LuaValue.valueOf(false);
            }
        }
    }

    static class removeUserGroup extends TwoArgFunction {
        public Computer computer;

        public removeUserGroup(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue userName, LuaValue groupName) {
            if (!computer.hasRootPerms(computer.currUser) || computer.allUsers.get(userName.checkjstring()) == null) {
                return LuaValue.valueOf(false);
            }
            User user = computer.allUsers.get(userName.checkjstring());
            for (UserGroup i : computer.groups) {
                if (i.groupName.equals(groupName.checkjstring())) {
                    i.remove(userName.checkjstring());
                    user.groups.remove(i);
                    return LuaValue.valueOf(true);
                }
            }
            return LuaValue.valueOf(false);
        }
    }

    static class setPrimaryUserGroup extends TwoArgFunction {
        public Computer computer;

        public setPrimaryUserGroup(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue userName, LuaValue newGroup) {
            if (!computer.hasRootPerms(computer.currUser) || computer.allUsers.get(userName.checkjstring()) == null) {
                return LuaValue.valueOf(false);
            }
            User user = computer.allUsers.get(userName.checkjstring());
            UserGroup group = null;
            for (UserGroup i : computer.groups) {
                if (i.groupName.equals(newGroup.checkjstring())) {
                    group = i;
                    break;
                }
            }
            if (user == null || group == null) {
                return LuaValue.valueOf(false);
            }
            user.group = group;
            return LuaValue.valueOf(true);
        }
    }

    static class userExists extends OneArgFunction {
        public Computer computer;

        public userExists(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue userName) {
            return LuaValue.valueOf(computer.allUsers.get(userName.checkjstring()) != null);
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

    // gets groups specified user belongs to
    static class getGroups extends OneArgFunction {
        public Computer computer;

        public getGroups(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue userName) {
            LuaTable table = LuaValue.tableOf();
            if (userName.checkjstring().equals("-a")) {
                List<UserGroup> groups = computer.groups;
                for (UserGroup i : groups) {
                    table.insert(table.length(), LuaValue.valueOf(i.groupName));
                }
                return table;
            }
            User user = computer.allUsers.get(userName.checkjstring());
            if (user == null) {
                return null;
            }
            List<UserGroup> groups = user.groups;

            for (UserGroup i : groups) {
                table.insert(table.length(), LuaValue.valueOf(i.groupName));
            }

            return table;
        }
    }

    static class removeGroup extends OneArgFunction {
        public Computer computer;

        public removeGroup(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue groupName) {
            if (!computer.hasRootPerms(computer.currUser)) {
                return LuaValue.valueOf(false);
            }
            for (UserGroup i : computer.groups) {
                if (i.groupName.equals(groupName.checkjstring())) {
                    computer.groups.remove(i);
                    return LuaValue.valueOf(true);
                }
            }
            return LuaValue.valueOf(false);
        }
    }

    static class addGroup extends OneArgFunction {
        public Computer computer;

        public addGroup(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue groupName) {
            if (!computer.hasRootPerms(computer.currUser)) {
                return LuaValue.valueOf(false);
            }
            computer.groups.add(new UserGroup(groupName.checkjstring(), computer));
            return LuaValue.valueOf(true);
        }
    }

    static class getCurrUser extends ZeroArgFunction {
        public Computer computer;

        public getCurrUser(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(computer.currUser.uName);
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

    static class triggerEvent extends OneArgFunction {
        public Computer computer;

        public triggerEvent(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue eventName) {
            computer.eventTriggered(new Event(eventName.tojstring()));
            return null;
        }
    }

    static class sendPacket extends ThreeArgFunction {
        public Computer computer;

        public sendPacket(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue protocol, LuaValue dst, LuaValue data) {
            List<String> buffer = new ArrayList<>();
            if (data.istable() && data.checktable().length() > 0) {
                LuaTable table = data.checktable();
                for (int i = 1; i <= table.length(); i++) { // send ping localhost test test
                    if (!table.get(LuaValue.valueOf(i)).isnil()) {
                        buffer.add(table.get(LuaValue.valueOf(i)).checkjstring());
                    }
                }
            }
            return LuaValue.valueOf(computer.router.sendPacket(dst.checkjstring(), computer.address, protocol.checkjstring(), buffer));
        }
    }

    static class countTable extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue table) {
            try {
                if (!table.istable()) {
                    return LuaValue.valueOf(0);
                }
                return LuaValue.valueOf(table.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    static class registerCommand extends OneArgFunction {
        public Computer computer;

        public registerCommand(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue luaValue) {
            String path = luaValue.checkjstring();
            L_File file = computer.filesystem.getFile(path);
            if (file == null) {
                return LuaValue.valueOf(false);
            }
            Command command = new Command(file);
            if (!computer.commands.contains(command)) {
                computer.commands.add(command);
                return LuaValue.valueOf(true);
            }
            return LuaValue.valueOf(false);
        }
    }

    static class executeScript extends TwoArgFunction {
        Computer computer;

        public executeScript(Computer computer) {
            this.computer = computer;
        }

        @Override
        public LuaValue call(LuaValue scriptPath, LuaValue args) {
            try {
                String path = scriptPath.checkjstring();
                String[] buffer = path.split("/");
                if (computer.filesystem.getFile(path) != null) {
                    LuaUtil.run(path, new ProcessedText(buffer[buffer.length - 1] + " " + args.checkjstring()), computer);
                    return LuaValue.valueOf(true);
                }
                return LuaValue.valueOf(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return LuaValue.valueOf(false);
        }
    }
}
