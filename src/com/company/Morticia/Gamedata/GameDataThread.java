package com.company.Morticia.Gamedata;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.UI.GUI.Terminal.TerminalIO;

public class GameDataThread implements Runnable {
    private Thread thread;
    private final String threadName = "GameDataThread";

    /**
     * Starts thread to process the game data, such as executing machine behavior
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(this, threadName);
            thread.start();
        }
    }

    @Override
    public void run() {
        try {
            TerminalIO.setPrefix(Gamedata.playerComputer.generatePrefix());
            while (!thread.isInterrupted()) {
                Gamedata.tick();
            }
            Gamedata.threadClosed();
            System.out.println("Gamedata Thread Closed.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void stop() {
        thread.interrupt();
    }
}
