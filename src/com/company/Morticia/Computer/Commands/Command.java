package com.company.Morticia.Computer.Commands;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Computer.Filesystem.L_File;
import com.company.Morticia.Lua.LuaUtil;

public class Command {
    public String path;
    public String name;
    public L_File file;

    public Command(L_File file) {
        this.file = file;
        this.path = file.getPath();
        this.name = file.cName.replaceAll(".lua", "");
    }

    public void execute(Computer computer, ProcessedText pText) {
        LuaUtil.run(this.path, pText, computer);
    }
}
