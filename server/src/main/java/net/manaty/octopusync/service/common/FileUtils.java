package net.manaty.octopusync.service.common;

import java.io.File;
import java.nio.file.Path;

public class FileUtils {

    public static void createDirectory(Path path) {
        File file = path.toFile();
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IllegalStateException("Not a directory: " + path);
            }
        } else {
            if (!file.mkdirs()) {
                throw new IllegalStateException("Failed to create directory: " + path);
            }
        }
    }
}
