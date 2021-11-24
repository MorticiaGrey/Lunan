package com.company.Morticia.Lua;

import com.company.Morticia.Computer.Commands.ProcessedText;
import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Util.Constants;
import com.company.Morticia.Util.DiscUtils.DiscUtils;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseIoLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseStringLib;

public class LuaUtil {
    static Globals serverGlobals;

    public static void init() {
        serverGlobals = new Globals();
        serverGlobals.load(new JseBaseLib());
        serverGlobals.load(new PackageLib());
        serverGlobals.load(new JseStringLib());
        serverGlobals.load(new JseMathLib());

        LoadState.install(serverGlobals);
        LuaC.install(serverGlobals);

        LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);
    }

    public static void run(String script, ProcessedText in, Computer computer) {
        Globals userGlobals = new Globals();
        userGlobals.load(new JseBaseLib());
        userGlobals.load(new PackageLib());
        userGlobals.load(new Bit32Lib());
        userGlobals.load(new TableLib());
        userGlobals.load(new JseStringLib());
        userGlobals.load(new JseMathLib());

        userGlobals.load(new LunanLib(computer, script));
        userGlobals.set("print", new LunanLib.l_print());

        userGlobals.set("raw_input", LuaValue.valueOf(in.original));
        userGlobals.set("args", in.argsToLuaTable());
        userGlobals.set("flags", in.flagsToLuaTable());

        userGlobals.set("htmlSpace", LuaValue.valueOf(Constants.htmlSpace));
        userGlobals.set("htmlTab", LuaValue.valueOf(Constants.htmlSpace + Constants.htmlSpace + Constants.htmlSpace + Constants.htmlSpace));

        userGlobals.set("tableLength", new LunanLib.countTable());

        LoadState.install(userGlobals);
        LuaC.install(userGlobals);

        userGlobals.loadfile(DiscUtils.makeObjectivePath(computer.filesystem.path + script)).call();
    }

    // Simple read-only table whose contents are initialized from another table.
    static class ReadOnlyLuaTable extends LuaTable {
        public ReadOnlyLuaTable(LuaValue table) {
            presize(table.length(), 0);
            for (Varargs n = table.next(LuaValue.NIL); !n.arg1().isnil(); n = table
                    .next(n.arg1())) {
                LuaValue key = n.arg1();
                LuaValue value = n.arg(2);
                super.rawset(key, value.istable() ? new ReadOnlyLuaTable(value) : value);
            }
        }
        public LuaValue setmetatable(LuaValue metatable) { return error("table is read-only"); }
        public void set(int key, LuaValue value) { error("table is read-only"); }
        public void rawset(int key, LuaValue value) { error("table is read-only"); }
        public void rawset(LuaValue key, LuaValue value) { error("table is read-only"); }
        public LuaValue remove(int pos) { return error("table is read-only"); }
    }
}
