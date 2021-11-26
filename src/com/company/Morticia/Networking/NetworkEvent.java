package com.company.Morticia.Networking;

import com.company.Morticia.Computer.Commands.ProcessedText;
import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Events.Event;
import com.company.Morticia.Lua.LuaUtil;

import java.util.ArrayList;
import java.util.List;

public class NetworkEvent extends Event {
    public NetworkAddress src;
    public NetworkAddress dst;

    public String protocol;

    public List<String> data;

    public NetworkEvent(String eventName, String eventPath, NetworkAddress src, NetworkAddress dst, String protocol, List<String> data) {
        super(eventName, eventPath, new ArrayList<>());
        this.src = src;
        this.dst = dst;
        this.protocol = protocol;
        this.data = new ArrayList<>(data);
    }

    public NetworkEvent(String eventName, NetworkAddress src, NetworkAddress dst, String protocol, List<String> data) {
        super(eventName, new ArrayList<>());
        this.src = src;
        this.dst = dst;
        this.protocol = protocol;
        this.data = new ArrayList<>(data);
    }

    @Override
    public void triggerLuaScript(Computer computer) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(eventName).append(" ").append(src).append(" ").append(dst).append(" ").append(protocol);
        for (String i : data) {
            buffer.append(" ").append(i);
        }
        LuaUtil.run(scriptPath, new ProcessedText(buffer.toString()), computer);
    }
}
