package com.linkedin.replica.wall.commands;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.exceptions.WallException;

import java.text.ParseException;
import java.util.HashMap;

public abstract class Command {
    protected HashMap<String, Object> args;
    protected DatabaseHandler dbHandler;

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
    public abstract Object execute() throws NoSuchMethodException, IllegalAccessException, ParseException;

//    public void setRequest(HashMap<String, Object> request) {
//        this.request = request;
//    }
//
    public void setDbHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    protected void validateArgs(String[] requiredArgs) {
        JsonObject object = (JsonObject) args.get("request");
        for(String arg: requiredArgs)
            if(!object.keySet().contains(arg)) {
                String exceptionMsg = String.format("Cannot execute command. %s argument is missing", arg);
                throw new WallException(exceptionMsg);
            }
    }


}