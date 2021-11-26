package com.company.Morticia.Util.DiscUtils;

import com.company.Morticia.Util.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DiscUtils {
    // All paths passed as input will be relative to the master path
    public static String masterDir;
    public static Path masterPath;

    public static void init() {
        masterDir = System.getProperty("user.dir") + "/Gamedata";
        masterPath = Paths.get(masterDir);

        // Not using writeFolder() because writeFolder takes relative paths and this is an objective path
        File f = new File(masterDir);
        if (!f.exists()) {
            if (!f.mkdir()) {
                System.out.println("Error: could not create [" + masterDir + "] folder");
            }
        }

        writeFolder(Constants.computersPath);
        writeFolder(Constants.defaultFileSystemPath);
    }

    /**
     * Converts from relative paths to object paths. If already objective path nothing happens
     *
     * @param path Path to be operated on
     * @return Objective path
     */
    public static String makeObjectivePath(String path) {
        if (isObjectivePath(path)) {
            return path;
        } else if (path.startsWith("/")) {
            return masterDir + path;
        } else {
            return masterDir + "/" + path;
        }
    }

    public static boolean isObjectivePath(String path) {
        return path.startsWith(masterDir);
    }

    public static void writeFolder(String path) {
        File f = new File(makeObjectivePath(path));
        if (!f.exists()) {
            if (!f.mkdir()) {
                System.out.println("Error: could not create [" + makeObjectivePath(path) + "] folder");
            }
        }
    }

    public static boolean folderExists(String path) {
        return new File(makeObjectivePath(path)).exists();
    }

    // Yes this is the exact same as folderExists(), this exists to avoid me forgetting that down the line
    public static boolean fileExists(String path) {
        return new File(makeObjectivePath(path)).exists();
    }

    public static DiscFile writeFile(String path) {
        return new DiscFile(Paths.get(makeObjectivePath(path)));
    }

    public static DiscFile writeFile(String path, List<String> contents) {
        return new DiscFile(Paths.get(makeObjectivePath(path)), contents);
    }

    public static DiscFile writeFile(String path, String[] contents) {
        return new DiscFile(Paths.get(makeObjectivePath(path)), List.of(contents));
    }

    public static List<String> readFile(String path) {
        try {
            return Files.readAllLines(Paths.get(makeObjectivePath(path)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> readFile(File file) {
        try {
            return Files.readAllLines(Paths.get(file.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean appendToFile(String path, String a) {
        List<String> contents = readFile(makeObjectivePath(path));
        if (contents != null) {
            contents.add(a);
            writeFile(path, contents);
            return true;
        }
        return false;
    }

    public static File[] getDirChildren(String path) {
        try {
            File f = new File(makeObjectivePath(path));
            if (f.exists() && f.isDirectory()) {
                return f.listFiles();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void copyObject(String src, String dst) {
        try {
            Files.copy(Paths.get(makeObjectivePath(src)), Paths.get(makeObjectivePath(dst)), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFolder(String path) {
        try {
            Files.walk(Paths.get(makeObjectivePath(path)))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception ignored) {}
    }
}
