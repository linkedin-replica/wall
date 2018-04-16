package com.linkedin.replica.wall.commands.impl;

import java.util.LinkedHashMap;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.arangodb.velocypack.VPackParser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Reply;

public class DeleteReplyCommand extends Command{

    public DeleteReplyCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws ParseException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"replyId"});

        // call dbHandler to get error or success message from dbHandler
        Reply reply;
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        String replyId = args.get("replyId").toString();

        reply = dbHandler.getReply(replyId);
        boolean response = dbHandler.deleteReply(reply);
        return response;
    }
}

