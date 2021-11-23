package com.company.Morticia.UI;

import com.company.Morticia.Lua.LuaUtil;
import com.company.Morticia.UI.GUI.Terminal.TerminalIO;

public class UIThread implements Runnable {
    private Thread thread;
    private String threadName = "UIThread";

    /**
     * Starts thread to run the ui, namely to get user input and display output
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
            UI.start();
            while (!thread.isInterrupted()) {
                UI.tick();
            }
            System.out.println("UI Thread Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        thread.interrupt();
    }
}
