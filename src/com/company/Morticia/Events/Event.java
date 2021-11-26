package com.company.Morticia.Events;

import com.company.Morticia.Computer.Commands.ProcessedText;
import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Lua.LuaUtil;

import java.util.List;

public class Event {
    public String eventName;
    public String scriptPath;
    public List<String> args;

    public Event(String eventName, String eventPath, List<String> args) {
        this.eventName = eventName;
        this.scriptPath = eventPath;
        this.args = args;
    }

    public Event(String eventName, List<String> args) {
        this.eventName = eventName;
        this.scriptPath = "/events/" + eventName + ".lua";
        this.args = args;
    }

    public void triggerLuaScript(Computer computer) {
        StringBuilder input = new StringBuilder(eventName + " ");
        for (String i : args) {
            input.append(i + " ");
        }
        LuaUtil.run(scriptPath, new ProcessedText(input.toString()), computer);
    }
}
