package com.linkedin.replica.wall.cache.handlers;

import java.io.IOException;

public interface PostsCacheHandler extends CacheHandler {

    void cachePost(String postId,Object post) throws IOException, IllegalAccessException, InstantiationException;
    Object getPost (String postId, Class<?> postClass) throws IllegalAccessException, InstantiationException, NoSuchFieldException, IOException;
}
