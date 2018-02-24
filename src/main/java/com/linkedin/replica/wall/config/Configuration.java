package com.linkedin.replica.wall.config;

public class Configuration {
    private String databaseConfigPath;
    private String commandConfigPath;
    private static Configuration instance;

    private Configuration(String databaseConfigPath, String commandConfigPath) {
        this.databaseConfigPath = databaseConfigPath;
        this.commandConfigPath = commandConfigPath;
    }

    public static Configuration getInstance(String databaseConfigPath, String commandConfigPath) {

        if(instance == null){
            synchronized (Configuration.class) {
                if(instance == null){
                    instance = new Configuration(databaseConfigPath, commandConfigPath);
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

}








