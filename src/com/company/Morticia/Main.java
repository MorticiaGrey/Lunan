package com.company.Morticia;

import com.company.Morticia.Gamedata.GameDataThread;
import com.company.Morticia.Lua.LuaUtil;
import com.company.Morticia.UI.UIThread;
import com.company.Morticia.Util.DiscUtils.DiscUtils;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JsePlatform;

public class Main {
    public static GameDataThread gdThread;
    public static UIThread uiThread;

    public static void main(String[] args) {
        // Initialize project
        DiscUtils.init();
        LuaUtil.init();

        // Start threads
        gdThread = new GameDataThread();
        uiThread = new UIThread();

        gdThread.start();
        uiThread.start();

        //LuaUtil.run("/home/morticia/Projects/IdeaProjects/Lunan/src/com/company/Morticia/Lua/test.lua");
        //Globals test = JsePlatform.standardGlobals();
        //test.loadfile("/home/morticia/Projects/IdeaProjects/Lunan/src/com/company/Morticia/Lua/test.lua").call();
    }
}
