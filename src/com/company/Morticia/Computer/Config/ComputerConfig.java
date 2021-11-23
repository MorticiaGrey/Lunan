package com.company.Morticia.Computer.Config;

import com.company.Morticia.Util.Constants;
import com.company.Morticia.Util.DiscUtils.DiscUtils;

import java.util.ArrayList;
import java.util.List;

public class ComputerConfig {
    public String path;

    public String compName;
    public boolean acceptPlayerControl;

    /**
     * Takes a path to a file and interprets the text in it to convert it to easily accessible variables
     *
     * @param path Path to file with text
     */
    public ComputerConfig(String path) {
        List<String> text = DiscUtils.readFile(path);
        for (String i : text) {
            String[] line = i.split(" ");
            if (line.length > 2) {
                if (!line[0].isBlank() && !line[1].isBlank()) {
                    if (line[0].startsWith("name:")) {
                        this.compName = line[1];
                    } else if (line[0].startsWith("acceptPlayerControl:")) {
                        this.acceptPlayerControl = Boolean.parseBoolean(line[1]);
                    }
                }
            }
        }
    }

    public ComputerConfig(String compName, boolean acceptPlayerControl) {
        this.path = Constants.computersPath + "/" + compName + "/config";
        this.compName = compName;
        this.acceptPlayerControl = acceptPlayerControl;
        DiscUtils.writeFile(this.path, new String[]{"name: " + this.compName, "acceptPlayerControl: " + this.acceptPlayerControl});
    }
}
