package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Comment;

public class EditCommentCommand extends Command{

    public EditCommentCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"commentId", "authorId", "parentPostId"});

        // call dbHandler to get error or success message from dbHandler
        HashMap<String, Object> request = new HashMap<>();
        JsonObject requestArgs = (JsonObject) args.get("request");
        for(String key: requestArgs.keySet()) {
            switch (key) {
                case "likesCount":
                case "repliesCount": request.put(key, requestArgs.get(key).getAsInt());break;
                case "text":
                case "commentId":
                case "authorId":
                case "parentPostId": request.put(key, requestArgs.get(key).getAsString());break;
                case "commandName": break;
                default: break;
            }
        }

        String response =  dbHandler.editComment(request);
        return response;
    }
}

