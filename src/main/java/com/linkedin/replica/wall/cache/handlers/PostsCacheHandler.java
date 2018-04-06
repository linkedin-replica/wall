package com.linkedin.replica.wall.cache.handlers;

import java.io.IOException;

public interface PostsCacheHandler extends CacheHandler {

    void cachePost(String postId,Object post) throws IOException, IllegalAccessException, InstantiationException;
}
