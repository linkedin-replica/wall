package com.linkedin.replica.wall.commands;

import com.linkedin.replica.wall.cache.handlers.CacheHandler;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.exceptions.WallException;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

public abstract class Command {
    protected HashMap<String, Object> args;
    protected DatabaseHandler dbHandler;
    protected CacheHandler cacheHandler;

    public Command(HashMap<String, Object> args) {
        this.args = args;
    }

    public Command(HashMap<String, Object> args, DatabaseHandler dbHandler) {
        this.args = args;
        this.dbHandler = dbHandler;
    }


    /**
     * Execute the command.
     * @return The output (if any) of the command
     * 	LinkedHashMap preserve order of insertion so it will preserve this order when parsing to JSON
     */
    public abstract Object execute() throws NoSuchMethodException, IllegalAccessException, ParseException, NoSuchFieldException, IOException, InstantiationException;

//    public void setRequest(HashMap<String, Object> request) {
//        this.request = request;
//    }
//
    public void setDbHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    protected void validateArgs(String[] requiredArgs) {
        for(String arg: requiredArgs)
            if(!args.containsKey(arg)) {
                String exceptionMsg = String.format("Cannot execute command. %s argument is missing", arg);
                throw new WallException(exceptionMsg);
            }
    }


}