package com.linkedin.replica.wall.commands;

import com.linkedin.replica.wall.handlers.DatabaseHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class Command {
    protected HashMap<String, String> request;
    protected DatabaseHandler dbHandler;

    public Command(HashMap<String, String> request) {
        this.request = request;
    }
    /**
     * Execute the command.
     * @return The output (if any) of the command
     */

    public abstract LinkedHashMap<String, Object> execute();

    public void setArgs(HashMap<String, String> request) {
        this.request = request;
    }

    public void setDbHandler(DatabaseHandler dbHandler){
        this.dbHandler = dbHandler;
    }
}