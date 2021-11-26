package com.company.Morticia.Networking;

import java.util.ArrayList;
import java.util.List;

public class NetworkRegistry {
    public static List<NetworkListener> globalNetworkRegistry = new ArrayList<>();
    public static List<Router> globalRouterRegistry;

    public static boolean addListener(NetworkListener listener) {
        if (getListener(listener.getAddress().address) == null) {
            globalNetworkRegistry.add(listener);
            return true;
        }
        return false;
    }

    public static NetworkListener getListener(String address) {
        for (NetworkListener i : globalNetworkRegistry) {
            if (i.getAddress().address.equals(address)) {
                return i;
            }
        }
        return null;
    }

    public static Router getRouter(String routerName) {
        for (Router i : globalRouterRegistry) {
            if (i.routerName.equals(routerName)) {
                return i;
            }
        }
        return null;
    }
}
