package com.company.Morticia.Computer;

import com.company.Morticia.Computer.Commands.Command;
import com.company.Morticia.Computer.Commands.ProcessedText;
import com.company.Morticia.Computer.Config.ComputerConfig;
import com.company.Morticia.Computer.Filesystem.Filesystem;
import com.company.Morticia.Computer.Filesystem.Folder;
import com.company.Morticia.Computer.User.User;
import com.company.Morticia.Computer.User.UserGroup;
import com.company.Morticia.Events.Event;
import com.company.Morticia.Events.LunanEventListener;
import com.company.Morticia.Gamedata.Scenario;
import com.company.Morticia.Networking.NetworkAddress;
import com.company.Morticia.Networking.NetworkEvent;
import com.company.Morticia.Networking.NetworkListener;
import com.company.Morticia.Networking.Router;
import com.company.Morticia.UI.GUI.Terminal.TerminalIO;
import com.company.Morticia.Util.Constants;
import com.company.Morticia.Util.DiscUtils.DiscUtils;

import java.util.ArrayList;
import java.util.List;

public class Computer implements NetworkListener, LunanEventListener {
    public static int numOfComputers = 0;

    public ComputerConfig config;

    public boolean isPlayerComputer;

    public int id;
    public String compName;
    public String path;

    public Filesystem filesystem;
    public Folder currFolder;

    public List<String> input;

    public List<Command> commands;

    public String userSavePath;
    public List<UserGroup> groups;
    public UserGroup allUsers;
    public User rootUser;
    public User currUser;

    public Router router;
    public NetworkAddress address;

    public Computer(String compName, Scenario scenario, Router router) {
        this.id = numOfComputers;
        numOfComputers += 1;
        this.isPlayerComputer = false;
        //this.path = Constants.computersPath + "/" + scenario.scenarioName + "/" + compName;
        if (Constants.debugMode || !scenario.playedBefore) {
            this.path = "/" + scenario.scenarioName + "/" + compName;
        } else {
            this.path = Constants.computersPath + "/" + scenario.scenarioName + "/" + compName;
        }
        DiscUtils.writeFolder(Constants.computersPath + "/" + scenario.scenarioName + "/" + compName);
        this.compName = compName;
        this.input = new ArrayList<>();
        this.commands = new ArrayList<>();

        this.groups = new ArrayList<>();
        this.allUsers = new UserGroup("all_users", this);
        this.rootUser = new User("root", "123root", this);
        this.currUser = this.rootUser;
        this.allUsers.add(this.rootUser);
        this.userSavePath = this.path + "/users";

        this.groups.add(allUsers);

        this.router = router;
        this.address = new NetworkAddress(this);

        if (DiscUtils.folderExists(this.path)) {
            // Load data from config file, load files into filesystem class
            config = new ComputerConfig(this);
            this.filesystem = new Filesystem(this);
            this.filesystem.load();
        } else {
            // Create default config file, make new default filesystem
            DiscUtils.writeFolder(this.path);
            config = new ComputerConfig(this, true);
            this.filesystem = new Filesystem(this);
            this.filesystem.initDefault();
            DiscUtils.writeFile(userSavePath);
        }
        this.filesystem.initDefaultCommands();
        rootUser.homeDir.setUserPermissions("rwx/rwx/r--");

        loadUsers();
        this.address.load();
        this.path = Constants.computersPath + "/" + scenario.scenarioName + "/" + compName;
        this.userSavePath = this.path + "/users";
        save();
    }

    public void processCommand(ProcessedText in) {
        if (in.command.startsWith("./")) {
            in.args.add(0, in.command.replaceFirst("./", ""));
            in.command = "execute";
        } else if (in.command.equals("sudo")) {
            if (rootUser.password.equals(TerminalIO.nextLine("[sudo] password for root: "))) {
                in.command = in.args.get(0);
                in.args.remove(0);
                currUser.sudoEnabled = true;
            } else {
                TerminalIO.println("incorrect password");
                return;
            }
        }
        for (Command i : commands) {
            if (i.name.equals(in.command) && i.file.canExecute(this.currUser)) {
                i.execute(this, in);
                currUser.sudoEnabled = false;
                return;
            }
        }
        currUser.sudoEnabled = false;
        TerminalIO.println(in.command + ": command not found");
    }

    public void tick() {
        if (!input.isEmpty()) {
            processCommand(new ProcessedText(input.get(0)));
            input.remove(0);
            TerminalIO.setPrefix(generatePrefix());
        }
    }

    public String generatePrefix() {
        return TerminalIO.wrapInColor("[" + currUser.uName + "@" + compName + " ", "#00E268") +
                TerminalIO.wrapInColor(currFolder.cName, "#FFFFFF") + TerminalIO.wrapInColor("]$&nbsp;", "#00E268");// &nbsp; forces html to add whitespace, so it won't just ignore the space on the end of the input
    }

    public boolean hasRootPerms(User user) {
        return user.sudoEnabled || user.equals(rootUser);
    }

    public UserGroup getGroup(String gName) {
        for (UserGroup i : groups) {
            if (i.groupName.equals(gName)) {
                return i;
            }
        }
        return null;
    }

    // Saves users and groups to text file
    public void saveUsers() {
        DiscUtils.writeFolder(this.path);
        List<String> content = new ArrayList<>();

        for (UserGroup i : groups) {
            if (!i.groupName.equals("all_users") && !i.groupName.equals("root")) {
                StringBuilder buffer = new StringBuilder("group: " + i.groupName + " ");
                for (User j : i.members) {
                    buffer.append(j.uName).append(",");
                }
                content.add(buffer.toString());
            }
        }

        content.add("");

        for (User i : allUsers.members) {
            if (!i.uName.equals("root")) {
                StringBuilder buffer = new StringBuilder("user: " + i.uName + " " + i.password + " ");
                for (UserGroup j : i.groups) {
                    buffer.append(j.groupName).append(",");
                }
                content.add(buffer.toString());
            }
        }

        DiscUtils.writeFile(userSavePath, content);
    }

    // Loads users from text file if it exists
    public void loadUsers() {
        List<String> data = DiscUtils.readFile(userSavePath);
        if (data == null) {
            return;
        }
        // Groups are made first so users can be added to them as they're loaded
        for (String i : data) {
            if (i.startsWith("group: ")) {
                String[] buffer = i.split(": ");
                String[] args = buffer[1].split(" ");
                this.groups.add(new UserGroup(args[0], this));
            } else if (i.startsWith("user: ")) {
                String[] buffer = i.split(": ");
                String[] args = buffer[1].split(" ");
                User user = new User(args[0], args[1], this);
                this.allUsers.add(user);
                String[] userGroups = args[2].split(",");
                for (String j : userGroups) {
                    for (UserGroup k : groups) {
                        if (k.groupName.equals(j) && !user.inGroup(k.groupName)) {
                            k.add(user);
                            user.groups.add(k);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void save() {
        DiscUtils.writeFolder(path + "/filesystem");
        this.filesystem.save();
        this.saveUsers();
        this.config.save();
        this.address.save();
    }

    @Override
    public NetworkAddress getAddress() {
        return address;
    }

    @Override
    public Router getRouter() {
        return router;
    }

    @Override
    public void networkEventTriggered(NetworkEvent event) {
        event.triggerLuaScript(this);
    }

    @Override
    public void eventTriggered(Event event) {
        event.triggerLuaScript(this);
    }
}
