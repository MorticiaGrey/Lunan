package com.company.Morticia.Networking;

import com.company.Morticia.Events.Event;

import java.util.ArrayList;
import java.util.List;

public class NetworkEvent extends Event {
    public NetworkAddress src;
    public NetworkAddress dst;

    public String protocol;

    public List<String> data;

    public NetworkEvent(String eventName, String eventPath, NetworkAddress src, NetworkAddress dst, String protocol, List<String> data) {
        super(eventName, eventPath);
        this.src = src;
        this.dst = dst;
        this.protocol = protocol;
        this.data = new ArrayList<>(data);
    }

    public NetworkEvent(String eventName, NetworkAddress src, NetworkAddress dst, String protocol, List<String> data) {
        super(eventName);
        this.src = src;
        this.dst = dst;
        this.protocol = protocol;
        this.data = new ArrayList<>(data);
    }
}
