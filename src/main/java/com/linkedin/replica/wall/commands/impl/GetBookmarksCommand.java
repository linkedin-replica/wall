package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.models.Bookmark;

public class GetBookmarksCommand extends Command{
    public GetBookmarksCommand() {
        super();
    }

    public LinkedHashMap<String, Object> execute() {
        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

        if (request.containsKey("userId")) {
            String userId = request.get("userId");
            response.put("response", dbHandler.getUserBookmarks(userId));
        } else {
            response.put("response", "missing information");
        }


        return response;
    }


}
