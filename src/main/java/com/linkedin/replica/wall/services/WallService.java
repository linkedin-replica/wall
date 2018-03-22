
package com.linkedin.replica.wall.services;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;


/**
 * Search Service is responsible for taking input from controller, reading commands config file to
 * get specific command responsible for handling input request and also get DatabaseHandler name
 * Associated with this command
 *
 * It will call command execute method after passing to its DatabaseHandler
 */
public class WallService {
    private Configuration config = Configuration.getInstance();

    public  Object serve(String commandName, HashMap<String, Object> args) throws Exception {
        Class<?> dbHandlerClass = config.getHandlerClass(commandName);
        DatabaseHandler dbHandler = (DatabaseHandler) dbHandlerClass.newInstance();

        Class<?> commandClass = config.getCommandClass(commandName);
        Constructor constructor = commandClass.getConstructor(new Class<?>[]{HashMap.class, DatabaseHandler.class});
        Command command = (Command) constructor.newInstance(args,dbHandler);

        return command.execute();
    }
}