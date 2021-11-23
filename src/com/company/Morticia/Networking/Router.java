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
        for (NetworkListener i : children) {
            if (i.getAddress().address.equals(dst)) {
                i.networkEventTriggered(new NetworkEvent("newPacket", src, i.getAddress(), protocol, data));
                return true;
            }
        }
        NetworkListener i = NetworkRegistry.getListener(dst);
        if (i != null) {
            i.networkEventTriggered(new NetworkEvent("newPacket", src, i.getAddress(), protocol, data));
            return true;
        }
        return false;
    }
}
