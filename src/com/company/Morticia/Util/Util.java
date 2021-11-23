package com.company.Morticia.Util;

import com.company.Morticia.Main;

public class Util {
    // Stops this program
    public static void terminate() {
        Main.gdThread.stop();
        Main.uiThread.stop();
    }
}
