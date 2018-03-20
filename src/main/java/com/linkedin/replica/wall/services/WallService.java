
package com.linkedin.replica.wall.services;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.handlers.DatabaseHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * Main Service is responsible for taking input from controller, reading commands arango_config file to
 * get specific command responsible for handling input request and also get DatabaseHandler name
 * Associated with this command
 *
 * It will call command execute method after passing to its DatabaseHandler
 */
public class WallService {
    private Configuration config;


    public WallService() throws IOException{
        config = Configuration.getInstance();
    }


    public Object serve(String commandName, HashMap<String, String> args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ParseException {
        Class<?> commandClass = config.getCommandClass(commandName);
        Constructor constructor = commandClass.getConstructor(HashMap.class);
        Command command = (Command) constructor.newInstance(args);

        Class<?> dbHandlerClass = config.getHandlerClass(commandName);
        DatabaseHandler dbHandler = (DatabaseHandler) dbHandlerClass.newInstance();

        command.setDbHandler(dbHandler);

        return command.execute();
    }

}