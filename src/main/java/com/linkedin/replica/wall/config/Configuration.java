package com.linkedin.replica.wall.config;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.handlers.DatabaseHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private Properties commandConfig = new Properties();
    private Properties appConfig = new Properties();
    private Properties arangoConfig = new Properties();

    private static Configuration instance;

    private Configuration(String appConfigPath, String arangoConfigPath, String commandsConfigPath) throws IOException {
        populateWithConfig(appConfigPath, appConfig);
        populateWithConfig(arangoConfigPath, arangoConfig);
        populateWithConfig(commandsConfigPath, commandConfig);
    }

    private static void populateWithConfig(String configFilePath, Properties properties) throws IOException {
        FileInputStream inputStream = new FileInputStream(configFilePath);
        properties.load(inputStream);
        inputStream.close();
    }

    public static void init(String appConfigPath, String arangoConfigPath, String commandsConfigPath) throws IOException {
        instance = new Configuration(appConfigPath, arangoConfigPath, commandsConfigPath);
    }

    public static Configuration getInstance() throws IOException {
        return instance;
    }

    public Class getCommandClass(String commandName) throws ClassNotFoundException {
        String commandsPackageName = Command.class.getPackage().getName() + ".impl";
        String commandClassPath = commandsPackageName + '.' + commandConfig.get(commandName + ".command");
        System.out.println(commandConfig.toString());
        System.out.println("xxx" + commandClassPath);
        return Class.forName(commandClassPath);
    }

    public Class getHandlerClass(String commandName) throws ClassNotFoundException {
        String handlerPackageName = DatabaseHandler.class.getPackage().getName() + ".impl";
        String handlerName = (String) commandConfig.get(commandName + ".handler");
        if(handlerName == null)
            handlerName = "ArangoWallHandler";
        String handlerClassPath = handlerPackageName + "." + handlerName;
        return Class.forName(handlerClassPath);
    }

    public String getAppConfig(String key) {
        return appConfig.getProperty(key);
    }

    public String getArangoConfig(String key) {
        return arangoConfig.getProperty(key);
    }
}








