package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.database.DatabaseConnection;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.database.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.config.Configuration;

public class AddCommentCommand extends Command{


    public AddCommentCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"authorId", "parentPostId", "text"});


        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String authorId = request.get("authorId").getAsString();
        String parentPostId = request.get("parentPostId").getAsString();
        String text = request.get("text").getAsString();
        Long timestamp = System.currentTimeMillis();

        Comment comment = new Comment();
        comment.setCommentId(UUID.randomUUID().toString());
        comment.setAuthorId(authorId);
        comment.setParentPostId(parentPostId);
        comment.setText(text);
        comment.setTimestamp(timestamp);
        boolean response =  dbHandler.addComment(comment);
        return response;
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
		String rootFolder = "src/main/resources/";
		Configuration.init(rootFolder + "app.config", rootFolder + "arango.test.config",
				rootFolder + "commands.config", rootFolder + "controller.config", rootFolder + "cache.config");

		DatabaseConnection.init();
		ArangoWallHandler handler = new ArangoWallHandler();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		JsonObject obj = new JsonObject();
		obj.addProperty("authorId", "21112412");
		obj.addProperty("text", "asfflnlakdnakfnw");
		obj.addProperty("parentPostId", "2751590");
		
		map.put("request", obj);
		AddCommentCommand command = new AddCommentCommand(map, handler);
		command.execute();
	}
}

