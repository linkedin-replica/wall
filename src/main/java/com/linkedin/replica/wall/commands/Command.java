package com.linkedin.replica.wall.commands;

import com.linkedin.replica.wall.handlers.DatabaseHandler;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class Command {
    protected HashMap<String, String> request;
    protected DatabaseHandler dbHandler;

    public Command(HashMap<String, String> args) {
        this.request = args;
    }


    /**
     * Execute the command.
     * @return The output (if any) of the command
     * 	LinkedHashMap preserve order of insertion so it will preserve this order when parsing to JSON
     */
    public abstract LinkedHashMap<String, Object> execute() throws ParseException;

    public void setRequest(HashMap<String, String> request) {
        this.request = request;
    }

    public void setDbHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }


}