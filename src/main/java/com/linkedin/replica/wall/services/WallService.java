
package com.linkedin.replica.wall.services;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.handlers.DatabaseHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * Wall Service is responsible for taking input from controller, reading commands db_config file to
 * get specific command responsible for handling input request and also get DatabaseHandler name
 * Associated with this command
 *
 * It will call command execute method after passing to its DatabaseHandler
 */
public class WallService {
    // load db_config file
    private Properties prop;
    private String commandsPackageName;
    private String dbHandlerPackageName;

    public WallService() throws FileNotFoundException, IOException{
        prop = new Properties();
        prop.load(new FileInputStream(Configuration.getInstance().getCommandConfigPath()));
        commandsPackageName = "com.linkedin.replica.wall.commands.impl";
        dbHandlerPackageName = "com.linkedin.replica.wall.handlers.impl";
    }

    public  LinkedHashMap<String, Object> serve(String commandName, HashMap<String, String> request) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException {
        String commandClassName = commandsPackageName + "." + prop.getProperty(commandName + ".command");
        String handlerClassName = dbHandlerPackageName + ".ArangoWallHandler";

        // load class of type command and create an instance
        Class c = Class.forName(commandClassName);
        Object o = c.newInstance();
        Command command = (Command) o;

        // load class of type database handler
        c = Class.forName(handlerClassName);
        o = c.newInstance();
        DatabaseHandler dbHandler = (DatabaseHandler) o;

        // set args and dbHandler of command
        command.setRequest(request);
        command.setDbHandler(dbHandler);

        // execute command
        return command.execute();
    }
}