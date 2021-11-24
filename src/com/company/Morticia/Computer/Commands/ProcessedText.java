package com.company.Morticia.Computer.Commands;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

public class ProcessedText {
    public String original;
    public String command;
    public List<String> args;
    public List<String> flags;

    public ProcessedText(String in) {
        this.original = in;
        this.args = new ArrayList<>();
        this.flags = new ArrayList<>();
        List<String> buffer = new ArrayList<>(List.of(in.split(" ")));
        this.command = buffer.get(0);

        buffer.remove(0);

        if (buffer.size() > 1) {
            for (String i : buffer) {
                if (i.startsWith("-")) {
                    this.flags.add(i);
                } else {
                    this.args.add(i);
                }
            }
        }
    }

    public LuaTable argsToLuaTable() {
        LuaTable table = LuaValue.tableOf();
        for (String i : this.args) {
            table.insert(0, LuaValue.valueOf(i));
        }
        return table;
    }

    public LuaTable flagsToLuaTable() {
        LuaTable table = LuaValue.tableOf();
        for (String i : this.flags) {
            table.insert(table.length(), LuaValue.valueOf(i));
        }
        return table;
    }
}
