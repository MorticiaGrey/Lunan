package com.company.Morticia.Gamedata;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Networking.Router;
import com.company.Morticia.UI.GUI.Terminal.TerminalIO;

import java.util.ArrayList;
import java.util.List;

public class Gamedata {
    // This will be changed with the implementation of scenarios, for bug testing it's faster though
    public static Router playerRouter = new Router("playerRouter", null);
    public static Computer playerComputer = new Computer("DebugComputer", playerRouter);
    public static List<Computer> computers = new ArrayList<>();

    public static void tick() {
        playerComputer.tick();
        for (Computer i : computers) {
            i.tick();
        }
    }

    /**
     * Called by UIThread when new input has been passed and needs to be processed
     *
     * @param input Input to be handled
     */
    public static void handleInput(String input) {
        TerminalIO.println(playerComputer.generatePrefix() + " " + input);
        playerComputer.input.add(input);
    }
}
