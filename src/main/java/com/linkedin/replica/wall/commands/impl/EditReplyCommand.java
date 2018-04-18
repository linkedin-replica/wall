package com.linkedin.replica.wall.commands.impl;

import java.text.ParseException;
import java.util.*;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Reply;

public class EditReplyCommand extends Command{

    public EditReplyCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws ParseException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"replyId", "authorId", "parentPostId", "parentCommentId"});
        validateArgs(new String[]{"replyId", "authorId", "parentPostId", "parentCommentId"});
        HashMap<String, Object> request = new HashMap<>();
        JsonObject requestArgs = (JsonObject) args.get("request");
        for(String key: requestArgs.keySet()) {
            switch (key) {
                case "likesCount": request.put(key, requestArgs.get(key).getAsInt());break;
                case "text":
                case "replyId":
                case "authorId":
                case "parentCommentId":
                case "parentPostId": request.put(key, requestArgs.get(key).getAsString());break;
                case "commandName": break;
                default: break;
            }
        }

        boolean response = dbHandler.editReply(args);
        return response;
    }
}
