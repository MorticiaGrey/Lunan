package com.company.Morticia.Networking;

import java.util.ArrayList;
import java.util.List;

public class Router {
    public String routerName;

    public Router parent;
    public List<NetworkListener> children;

    public Router(String name, Router parent) {
        this.routerName = name;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public boolean sendPacket(String dst, NetworkAddress src, String protocol, List<String> data) {
        if (dst.equals("localhost")) {
            src.listener.networkEventTriggered(new NetworkEvent(protocol, src, src, protocol, data));
            return true;
        }
        for (NetworkListener i : children) {
            if (i.getAddress().address.equals(dst)) {
                i.networkEventTriggered(new NetworkEvent(protocol, src, i.getAddress(), protocol, data));
                return true;
            }
        }
        NetworkListener i = NetworkRegistry.getListener(dst);
        if (i != null) {
            i.networkEventTriggered(new NetworkEvent(protocol, src, i.getAddress(), protocol, data));
            return true;
        }
        return false;
    }

    public void save() {
        for (NetworkListener i : children) {
            i.getAddress().save();
        }
    }

    public void load() {

    }
}
