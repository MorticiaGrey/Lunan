package com.company.Morticia.Events;

import com.company.Morticia.Computer.Commands.ProcessedText;
import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Lua.LuaUtil;

public class Event {
    public String eventName;
    public String scriptPath;

    public Event(String eventName, String eventPath) {
        this.eventName = eventName;
        this.scriptPath = eventPath;
    }

    public Event(String eventName) {
        this.eventName = eventName;
        this.scriptPath = "/events" + eventName;
    }

    public void triggerLuaScript(Computer computer) {
        LuaUtil.run(scriptPath, new ProcessedText(eventName), computer);
    }
}
