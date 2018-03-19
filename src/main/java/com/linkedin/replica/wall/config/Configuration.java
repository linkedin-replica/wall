package com.linkedin.replica.wall.config;

public class Configuration {
    private String databaseConfigPath;
    private String commandConfigPath;
    private String messagingConfigPath;
    private static Configuration instance;

    private Configuration(String databaseConfigPath, String commandConfigPath, String messagingConfigPath) {
        this.databaseConfigPath = databaseConfigPath;
        this.commandConfigPath = commandConfigPath;
        this.messagingConfigPath = messagingConfigPath;

    }

    public static Configuration getInstance(String databaseConfigPath, String commandConfigPath, String messagingConfigPath) {

        if(instance == null){
            synchronized (Configuration.class) {
                if(instance == null){
                    instance = new Configuration(databaseConfigPath, commandConfigPath, messagingConfigPath);
                }
            }
        }
        return instance;
    }

    public static Configuration getInstance(){
        return instance;
    }

    public String getDatabaseConfigPath() {
        return databaseConfigPath;
    }

    public String getCommandConfigPath() {
        return commandConfigPath;
    }

    public String getMessagingConfigPath(){return messagingConfigPath; }
}








