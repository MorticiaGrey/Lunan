package com.company.Morticia.Gamedata;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.UI.GUI.Terminal.TerminalIO;
import com.company.Morticia.UI.UI;
import com.company.Morticia.Util.Constants;

import java.util.List;

public class Gamedata {
    public static Computer playerComputer;
    public static List<Computer> computers;

    public static void threadStarted() {
        Scenario.initScenarios();
        Scenario currScenario = null;
        while (currScenario == null) {
            currScenario = Scenario.getCurrScenario();
        }
        UI.mainFrame.show();
        playerComputer = currScenario.defaultPlayerComputer;
        playerComputer.isPlayerComputer = true;
        computers = currScenario.computers;
        currScenario.load();
        if (!Constants.debugMode) {
            currScenario.playedBefore = true;
        }
    }

    public static void tick() {
        playerComputer.tick();
        for (Computer i : computers) {
            i.tick();
        }
    }

    public static void threadClosed() {
        /*playerComputer.filesystem.save();
        for (Computer i : computers) {
            i.filesystem.save();
        }
        playerComputer.config.save();
        playerComputer.saveUsers();*/
        Scenario.getCurrScenario().save();
    }

    /**
     * Called by UIThread when new input has been passed and needs to be processed
     *
     * @param input Input to be handled
     */
    public static void handleInput(String input) {
        TerminalIO.println(playerComputer.generatePrefix() + input);
        playerComputer.input.add(input);
    }
}
