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

    public int id;
    public String name;
    public String path;

    public Filesystem filesystem;
    public Folder currFolder;

    public List<String> input;

    public List<Command> commands;

    public List<UserGroup> groups;
    public UserGroup allUsers;
    public User rootUser;
    public User currUser;

    public Router router;
    public NetworkAddress address;

    public Computer(String name, Router router) {
        this.id = numOfComputers;
        numOfComputers += 1;
        this.path = Constants.computersPath + "/" + name;
        this.name = name;
        this.input = new ArrayList<>();
        this.commands = new ArrayList<>();

        this.groups = new ArrayList<>();
        this.allUsers = new UserGroup("all_users", this);
        this.rootUser = new User("root", "123root", this);
        this.currUser = this.rootUser;
        this.allUsers.add(this.rootUser);

        this.groups.add(allUsers);

        this.router = router;
        this.address = new NetworkAddress(this);

        // TODO: 11/20/21 Remove this later
        DiscUtils.deleteFolder(this.path);

        if (DiscUtils.folderExists(this.path)) {
            // Load data from config file, load files into filesystem class
            config = new ComputerConfig(this.path + "/config");
            this.filesystem = new Filesystem(this);
            this.filesystem.load();
        } else {
            // Create default config file, make new default filesystem
            DiscUtils.writeFolder(this.path);
            config = new ComputerConfig(name, true);
            this.filesystem = new Filesystem(this);
            this.filesystem.initDefault();
        }
        this.filesystem.initDefaultCommands();
        rootUser.homeDir.setUserPermissions("rwx/rwx/r--");
    }

    public void processCommand(ProcessedText in) {
        for (Command i : commands) {
            if (i.name.equals(in.command) && i.file.canExecute(this.currUser)) {
                i.execute(this, in);
                break;
            }
        }
    }

    public void tick() {
        if (!input.isEmpty()) {
            processCommand(new ProcessedText(input.get(0)));
            input.remove(0);
            TerminalIO.setPrefix(generatePrefix());
        }
    }

    public String generatePrefix() {
        return TerminalIO.getColor("#00E268") + "[" + this.currUser.uName + "@" + this.name + " "
                + TerminalIO.getColor("#FFFFFF") + this.currFolder.cName + TerminalIO.colorReset
                + TerminalIO.getColor("#00E268") + "]$&nbsp;" + TerminalIO.colorReset; // &nbsp; forces html to add whitespace, so it won't just ignore the space on the end of the input
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
