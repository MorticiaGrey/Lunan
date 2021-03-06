package com.company.Morticia.Gamedata;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Networking.NetworkRegistry;
import com.company.Morticia.Networking.Router;
import com.company.Morticia.UI.GUI.MainFrame;
import com.company.Morticia.UI.GUI.Terminal.TerminalGUI;
import com.company.Morticia.Util.Constants;
import com.company.Morticia.Util.DiscUtils.DiscUtils;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scenario {
    public Computer defaultPlayerComputer;
    public List<Computer> computers;

    List<Router> routers;

    public String scenarioName;
    public String savePath;

    public boolean playedBefore;

    public Scenario(String scenarioName) {
        this.scenarioName = scenarioName;
        this.savePath = "/" + scenarioName;
        this.computers = new ArrayList<>();
        this.routers = new ArrayList<>();
        this.playedBefore = false;

        DiscUtils.writeFolder(savePath);
        DiscUtils.writeFolder(Constants.computersPath + "/" + scenarioName);

        if (DiscUtils.fileExists(savePath + "/mem")) {
            List<String> data = DiscUtils.readFile(savePath + "/mem");
            if (data == null) {
                return;
            }
            for (String i : data) {
                if (i.startsWith("playedBefore: ")) {
                    String[] buffer = i.split(": ");
                    playedBefore = buffer[1].equals("true");
                }
            }
        } else {
            DiscUtils.writeFile(savePath + "/mem", new String[]{"playedBefore: " + playedBefore});
        }
    }

    public void writeComputerPaths() {
        DiscUtils.writeFolder(Constants.computersPath + "/" + scenarioName + "/" + defaultPlayerComputer.compName);
        for (Computer i : computers) {
            DiscUtils.writeFolder(Constants.computersPath + "/" + scenarioName + "/" + i.compName);
        }
    }

    public void save() {
        //defaultPlayerComputer.saveUsers();
        //defaultPlayerComputer.filesystem.saveTo(Constants.computersPath + "/" + scenarioName + "/" + defaultPlayerComputer.compName + "/filesystem", savePath);
        defaultPlayerComputer.save();
        for (Computer i : computers) {
            //i.saveUsers();
            //i.filesystem.saveTo(savePath + "/" + i.compName + "/filesystem", savePath);
            //DiscUtils.writeFolder(Constants.computersPath + "/" + scenarioName + defaultPlayerComputer.compName);
            i.save();
        }
        DiscUtils.writeFile(savePath + "/mem", new String[]{"playedBefore: " + playedBefore});
        List<String> content = new ArrayList<>();
        for (Router i : routers) {
            content.add(i.routerName);
        }
        DiscUtils.writeFile(savePath + "/routers", content);
    }

    public void loadDefault() {
        for (Computer i : computers) {
            i.loadUsers();
            i.filesystem.loadFrom(savePath + "/" + i.compName + "/filesystem", savePath + "/" + i.compName + "/perms");
        }
        if (DiscUtils.fileExists(savePath + "/mem")) {
            List<String> data = DiscUtils.readFile(savePath + "/mem");
            if (data == null) {
                return;
            }
            for (String i : data) {
                if (i.startsWith("playedBefore: ")) {
                    String[] buffer = i.split(": ");
                    playedBefore = buffer[1].equals("true");
                }
            }
        }
    }

    public void load() {
        List<String> data = DiscUtils.readFile(savePath + "/routers");

        if (data == null || data.isEmpty()) {
            return;
        }

        routers = new ArrayList<>();
        NetworkRegistry.globalRouterRegistry = new ArrayList<>();
        for (String i : data) {
            Router router = new Router(i, null);
            routers.add(router);
            NetworkRegistry.globalRouterRegistry.add(router);
        }

        Router router = NetworkRegistry.getRouter(defaultPlayerComputer.address.routerName);
        if (router != null) {
            defaultPlayerComputer.router = router;
            router.children.add(defaultPlayerComputer);
        }
        for (Computer i : computers) {
            router = NetworkRegistry.getRouter(i.address.routerName);
            if (router != null) {
                i.router = router;
                router.children.add(i);
            }
        }
    }

    public static List<Scenario> scenarios = new ArrayList<>();

    public static void initScenarios() {
        Scenario scenario1 = new Scenario("DebugScenario");

        Router playerRouter = new Router("player_router", null);

        scenario1.routers.add(playerRouter);

        scenario1.defaultPlayerComputer = new Computer("DebugComp", scenario1, playerRouter);
        if (Constants.debugMode) {
            DiscUtils.deleteFolder(scenario1.defaultPlayerComputer.path);
            DiscUtils.writeFolder(scenario1.defaultPlayerComputer.path);
        }
        //scenario1.defaultPlayerComputer.filesystem.loadFrom(scenario1.savePath + "/playerFilesystem", scenario1.savePath + "/playerPerms");
        scenario1.save();

        //scenario1.computers.add(new Computer("DebugCompPartner", scenario1, playerRouter));

        if (!scenario1.playedBefore) {
            scenario1.loadDefault();
        } else {
            scenario1.load();
        }

        //scenarios.add(scenario1);

        Scenario scenario2 = new Scenario("Induction");

        Router inductionRouter = new Router("HC", null);

        scenario2.defaultPlayerComputer = new Computer("Acetylene", scenario2, inductionRouter);
        scenario2.computers.add(new Computer("Ethanol", scenario2, inductionRouter));

        if (!scenario2.playedBefore) {
            scenario1.loadDefault();
        } else {
            scenario1.load();
        }

        scenarios.add(scenario2);

        Scenario trialScenario = new Scenario("Trial");

        // Player's router
        Router HN = new Router("HN", null);
        // Router of target network
        Router businessRouter = new Router("WesternConstructionHoldingsNetwork", null);

        trialScenario.defaultPlayerComputer = new Computer("Box", trialScenario, HN);

        // Proxy
        trialScenario.computers.add(new Computer("proxy", trialScenario, businessRouter));

        // Servers
        trialScenario.computers.add(new Computer("Server1", trialScenario, businessRouter));
        trialScenario.computers.add(new Computer("Server2", trialScenario, businessRouter));

        // Boss computer
        trialScenario.computers.add(new Computer("GsFfS1", trialScenario, businessRouter));

        // Worker computers
        trialScenario.computers.add(new Computer("Emp1", trialScenario, businessRouter));
        trialScenario.computers.add(new Computer("Emp2", trialScenario, businessRouter));
        trialScenario.computers.add(new Computer("Emp3", trialScenario, businessRouter));
        trialScenario.computers.add(new Computer("Emp4", trialScenario, businessRouter));

        scenarios.add(trialScenario);
    }

    public static Scenario getCurrScenario() {
        if (Constants.debugMode) {
            return scenarios.get(scenarios.size() - 1);
        }
        List<Object> possibilites = new ArrayList<>();
        possibilites.add("");

        int num = 1;
        for (Scenario i : scenarios) {
            possibilites.add(num + ": " + i.scenarioName);
            num++;
        }

        Icon icon = new ImageIcon();
        String input = (String) JOptionPane.showInputDialog(
                null,
                "Please select a level",
                "Level Selection",
                JOptionPane.PLAIN_MESSAGE,
                icon,
                possibilites.toArray(),
                "");

        for (Scenario i : scenarios) {
            if (i.scenarioName.equals(input)) {
                return i;
            }
        }
        return null;
    }
}
