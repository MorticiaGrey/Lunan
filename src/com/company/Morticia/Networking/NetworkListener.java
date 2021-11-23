package com.company.Morticia.Networking;

public interface NetworkListener {
    NetworkAddress getAddress();
    Router getRouter();
    void networkEventTriggered(NetworkEvent event);
}
