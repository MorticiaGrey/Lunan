package com.company.Morticia.Util.DiscUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Weird name so no one confuses it with another widely used class
public class DiscFile {
    public String parentFolderPath;
    public String name;
    public List<String> contents;

    public Path path;

    /**
     * Constructor for the DiscFile class. If file does not exist creates file, otherwise reads files contents into 'contents' variable
     *
     * @param path Path to the file this class represents.
     */
    public DiscFile(Path path) {
        this.parentFolderPath = path.getParent().toString();
        this.name = path.getFileName().toString();
        this.path = path;

        // If file exists get its contents, if not create new file
        File f = path.toFile();
        if (f.exists() && !f.isDirectory()) {
            try {
                this.contents = Files.readAllLines(path);
            } catch (Exception e) {
                System.out.println("Error when attempting to read [" + this.name + "] file. Error message as follows:");
                e.printStackTrace();
            }
        } else {
            try {
                if (!f.createNewFile()) {
                    System.out.println("Could not create file [" + this.path + "]");
                }
            } catch (Exception e) {
                System.out.println("Could not create file [" + this.path + "]. Error message as follows:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Constructor for the DiscFile class. If file does not exist file will be created and contents written to it.
     * Otherwise provided contents will be written anyway, potentially overwriting data.
     *
     * @param path Path to the file this class represents.
     * @param contents The contents to be written to this file. Each element represents a new line.
     */
    public DiscFile(Path path, List<String> contents) {
        this.parentFolderPath = path.getParent().toString();
        this.name = path.getFileName().toString();
        this.contents = new ArrayList<>(contents);
        this.path = path;

        // If file exists write new contents to it, if not create new file and write contents to it
        File f = path.toFile();
        if (f.exists() && !f.isDirectory()) {
            // write contents
            writeContents();
        } else {
            // create new file and write contents
            try {
                if (f.createNewFile()) {
                    writeContents();
                } else {
                    System.out.println("Could not create file [" + this.path + "]");
                }
            } catch (Exception e) {
                System.out.println("Could not create file [" + this.path + "]. Error message as follows:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes the buffer variable 'contents' to the actual file on the disc
     */
    public void writeContents() {
        StringBuilder contentsString = new StringBuilder();
        for (String i : contents) {
            contentsString.append(i).append("\n");
        }

        try {
            File f = path.toFile();
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contentsString.toString());
            bw.close();
        } catch (Exception e) {
            System.out.println("Could not write to file [" + this.name + "]. Error message as follows:");
            e.printStackTrace();
        }
    }
}
