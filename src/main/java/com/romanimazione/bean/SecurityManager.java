package com.romanimazione.bean;

import java.io.*;
import java.util.Properties;

public class SecurityManager {

    private static final String CONFIG_FILE = "security.properties";
    private static final String KEY_MASTER_CODE = "admin.master.code";

    private static SecurityManager instance;
    private Properties properties;

    private SecurityManager() {
        properties = new Properties();
        loadConfig();
    }

    public static synchronized SecurityManager getInstance() {
        if (instance == null) {
            instance = new SecurityManager();
        }
        return instance;
    }

    private boolean isTransient = false;

    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }
    
    public void reset() {
        properties.clear();
        if (!isTransient) {
            loadConfig();
        }
    }

    private void loadConfig() {
        if (isTransient) {
            return;
        }
        File file = new File(System.getProperty("user.dir"), CONFIG_FILE);
        if (file.exists()) {
            try (InputStream input = new FileInputStream(file)) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("SecManager: Failed to load config: " + e.getMessage());
            }
        }
    }

    private void saveConfig() {
        if (isTransient) return;
        
        File file = new File(System.getProperty("user.dir"), CONFIG_FILE);
        try (OutputStream output = new FileOutputStream(file)) {
            properties.store(output, "Security Configuration");
        } catch (IOException e) {
             System.err.println("SecurityManager: Failed to save config: " + e.getMessage());
        }
    }

    public boolean isMasterCodeSet() {
        return properties.containsKey(KEY_MASTER_CODE) && !properties.getProperty(KEY_MASTER_CODE).isEmpty();
    }

    public boolean verifyMasterCode(String inputCode) {
        String stored = properties.getProperty(KEY_MASTER_CODE);
        return stored != null && stored.equals(inputCode);
    }

    public void setMasterCode(String newCode) {
        if (newCode == null || newCode.length() < 64) {
            throw new IllegalArgumentException("Master Code must be at least 64 characters long.");
        }
        properties.setProperty(KEY_MASTER_CODE, newCode);
        saveConfig();
    }
}
