package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.models.Bookmark;

public class AddBookmarkCommand extends Command {


    public AddBookmarkCommand(HashMap<String, String> args) {
        super(args);
    }

    public LinkedHashMap<String, Object> execute() {
        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
        String errMsg;
        if (request.containsKey("userId") && request.containsKey("postId")) {
            String userId = request.get("userId");
            String postId = request.get("postId");
            Bookmark bookmark = new Bookmark(userId, postId);
            response.put("response", dbHandler.addBookmark(bookmark));
        } else {
            response.put("response", "missing information");
        }


        return response;
    }
}
