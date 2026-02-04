package com.romanimazione.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileResetter {

    private static final String[] FILES_TO_DELETE = {
        "users.json",
        "parties.json",
        "availabilities.json",
        "security.properties"
    };

    public static void main(String[] args) {
        System.out.println("Beginning File System Reset...");
        resetFiles();
    }

    public static void resetFiles() {
        boolean allDeleted = true;
        for (String filename : FILES_TO_DELETE) {
            File file = new File(filename);
            if (file.exists()) {
                System.out.println("Deleting " + filename + "...");
                try {
                    Files.delete(file.toPath());
                    System.out.println(" - Deleted.");
                } catch (IOException e) {
                    System.err.println(" ! Failed to delete " + filename + ": " + e.getMessage());
                    System.err.println("   (Ensure the application is CLOSED before running this reset)");
                    allDeleted = false;
                }
            } else {
                System.out.println("Skipping " + filename + " (Not found).");
            }
        }

        if (allDeleted) {
            System.out.println("\nReset Complete! All data files cleared.");
            System.out.println("Restart the application to re-initialize with default data.");
        } else {
            System.out.println("\nReset Completed with Errors.");
        }
    }
}
