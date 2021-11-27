package com.company.Morticia.Networking;

import com.company.Morticia.Computer.Computer;
import com.company.Morticia.Util.DiscUtils.DiscUtils;

import java.util.ArrayList;
import java.util.List;

public class NetworkAddress {
    public String address;
    public String routerName;
    public NetworkListener listener;

    public NetworkAddress(String address, NetworkListener listener) {
        this.address = address;
        this.listener = listener;
        this.routerName = listener.getRouter().routerName;
    }

    public NetworkAddress(NetworkListener listener) {
        this.listener = listener;
        this.routerName = listener.getRouter().routerName;
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

    public void save() {
        if (listener.getAddress().listener instanceof Computer computer) {
            String savePath = computer.path + "/networkData";
            List<String> contents = new ArrayList<>();

            contents.add("address: " + address);
            contents.add("router: " + listener.getRouter().routerName);

            DiscUtils.writeFile(savePath, contents);
        }
    }

    public void load() {
        if (listener.getAddress().listener instanceof Computer computer) {
            String savePath = computer.path + "/networkData";
            List<String> contents;
            try {
                contents = DiscUtils.readFile(savePath);
            } catch (Exception e) {
                DiscUtils.writeFile(savePath);
                e.printStackTrace();
                return;
            }

            if (contents == null) {
                return;
            }

            for (String i : contents) {
                if (i.startsWith("address: ")) {
                    String[] buffer = i.split(": ");
                    address = buffer[1];
                } else if (i.startsWith("router: ")) {
                    String[] buffer = i.split(": ");
                    routerName = buffer[1];
                }
            }
        }
    }

    @Override
    public String toString() {
        return address;
    }
}
