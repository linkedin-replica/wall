package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;

public class AddBookmarkCommand extends Command{

    public AddBookmarkCommand(HashMap<String, String> args) {
        super(args);
    }

    public String execute() {
        return null;
    }
}
