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
        String[] buffer = in.split(" ");
        this.command = buffer[0];

        if (buffer.length > 1) {
            for (int i = 1; i < buffer.length; i++) {
                if (!buffer[i].startsWith("-")) {
                    this.args.add(buffer[i]);
                } else {
                    this.flags.add(buffer[i]);
                }
            }
        }
    }

    public LuaTable argsToLuaTable() {
        LuaTable table = LuaValue.tableOf();
        for (String i : this.args) {
            table.insert(table.length(), LuaValue.valueOf(i));
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
