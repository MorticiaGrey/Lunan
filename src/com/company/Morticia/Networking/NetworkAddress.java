package com.company.Morticia.Networking;

public class NetworkAddress {
    public String address;
    public NetworkListener listener;

    public NetworkAddress(String address, NetworkListener listener) {
        this.address = address;
        this.listener = listener;
    }

    public NetworkAddress(NetworkListener listener) {
        this.listener = listener;
        boolean done = false;
        while (!done) {
            done = true;
            this.address = generateRandomIp();
            for (NetworkListener i : NetworkRegistry.globalNetworkRegistry) {
                if (i.getAddress().address.equals(this.address)) {
                    done = false;
                    break;
                }
            }
        }
    }

    private String getRandomInt(int min, int max) {
        return Integer.toString((int) Math.round(Math.random() * (max - min + 1) + min));
    }

    public String generateRandomIp() {
        return getRandomInt(0, 999) + "." + getRandomInt(0, 999) + "." + getRandomInt(0, 999) + "." + getRandomInt(0, 999);
    }
}
