package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.linkedin.replica.wall.commands.Command;

public class DeleteCommentCommand extends Command{

    public DeleteCommentCommand(HashMap<String, String> args) {
        super(args);
    }

    public LinkedHashMap<String, Object> execute() {
        return null;
    }
}

