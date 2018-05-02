package com.linkedin.replica.wall.cache.handlers;

import com.linkedin.replica.wall.models.ReturnedPost;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface PostsCacheHandler extends CacheHandler {

    void cachePost(String postId,Object post) throws IOException, IllegalAccessException, InstantiationException;
    Object getPost (String postId, Class<?> postClass) throws IllegalAccessException, InstantiationException, NoSuchFieldException, IOException;
    void deletePost (String postId);
    void editPost(String postId,HashMap<String, Object> args) throws IOException;
    Object getCompanyPosts(String companyId, int limit,Class<?> postClass) throws NoSuchFieldException, IllegalAccessException;
    void cacheCompanyPosts(String companyId,  List<ReturnedPost> returnedPosts) throws IllegalAccessException, IOException;
}
