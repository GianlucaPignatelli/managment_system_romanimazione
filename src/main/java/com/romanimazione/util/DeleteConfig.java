package com.romanimazione.util;

import java.io.File;

public class DeleteConfig {
    public static void main(String[] args) {
        String path = System.getProperty("user.dir");
        System.out.println("Working Directory: " + path);
        File file = new File(path, "security.properties");
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("SUCCESS: security.properties DELETED from " + file.getAbsolutePath());
            } else {
                System.out.println("ERROR: Failed to delete " + file.getAbsolutePath());
            }
        } else {
            System.out.println("INFO: security.properties NOT FOUND at " + file.getAbsolutePath());
        }
    }
}
