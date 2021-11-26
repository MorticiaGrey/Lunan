package com.company.Morticia.Gamedata;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Networking.NetworkRegistry;
import com.company.Morticia.Networking.Router;
import com.company.Morticia.Util.Constants;
import com.company.Morticia.Util.DiscUtils.DiscUtils;

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

    public void save() {
        //defaultPlayerComputer.saveUsers();
        //defaultPlayerComputer.filesystem.saveTo(Constants.computersPath + "/" + scenarioName + "/" + defaultPlayerComputer.compName + "/filesystem", savePath);
        defaultPlayerComputer.save();
        for (Computer i : computers) {
            //i.saveUsers();
            //i.filesystem.saveTo(savePath + "/" + i.compName + "/filesystem", savePath);
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

        if (data == null) {
            return;
        }

        routers = new ArrayList<>();
        NetworkRegistry.globalRouterRegistry = new ArrayList<>();
        for (String i : data) {
            Router router = new Router(i, null);
            routers.add(router);
            NetworkRegistry.globalRouterRegistry.add(router);
        }

        for (Computer i : computers) {
            Router router = NetworkRegistry.getRouter(i.address.routerName);
            if (router != null) {
                i.router = router;
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

        scenarios.add(scenario1);
    }

    public static Scenario getCurrScenario() {
        return scenarios.get(0);
    }
}
