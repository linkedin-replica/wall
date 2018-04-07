package com.linkedin.replica.wall.cache.handlers.impl;

import com.google.gson.Gson;
import com.linkedin.replica.wall.cache.Cache;
import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.models.Post;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

public class JedisCacheHandler implements PostsCacheHandler{

    private JedisPool jedisPool;
    private Configuration configuration;
    private int postDBIndex;
    private Gson gson;
    public JedisCacheHandler(){
        jedisPool = Cache.getInstance().getRedisPool();
        configuration = Configuration.getInstance();
        postDBIndex = Integer.parseInt(configuration.getRedisConfigProp("cache.post.index"));
        gson = new Gson();


    }

    @Override
    public void cachePost(String postId, Object post) throws IOException, IllegalAccessException, InstantiationException {

        Jedis cacheResource = jedisPool.getResource();
        Pipeline jedisPipeline = cacheResource.pipelined();
        cacheResource.select(postDBIndex);
        Class postClass = post.getClass();
        Field [] postFields = postClass.getDeclaredFields();
        for (int i = 0; i<postFields.length; i++){
            postFields[i].setAccessible(true);
            String fieldName = postFields[i].getName();
            Object value =  postFields[i].get(post);
            jedisPipeline.hset(postId,fieldName,gson.toJson(value));

        }
        System.out.println("post added in cache");
        jedisPipeline.sync();
        jedisPipeline.close();
        cacheResource.close();
    }

    @Override
    public Object getPost(String postId, Class<?> postClass) throws IllegalAccessException, InstantiationException, NoSuchFieldException, IOException {

        Jedis cacheResource = jedisPool.getResource();
        cacheResource.select(postDBIndex);
        if(!cacheResource.exists(postId)){
            return null;
        }

        Pipeline jedisPipeline = cacheResource.pipelined();
        Object post = postClass.newInstance();
        Field [] postFields = postClass.getDeclaredFields();
        for (int i = 0; i<postFields.length; i++){
            String fieldName = postFields[i].getName();
            String value = cacheResource.hget(postId,fieldName);
            Object objectValue = gson.fromJson(value,postFields[i].getType());

            try {
                Field field = postClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(post,objectValue);
            }
            catch (NoSuchFieldException e){
                throw new NoSuchFieldException(
                        "Could not find field named '" + fieldName + "' in class '" + postClass +
                                "'.  All fields: " + Arrays.asList(postClass.getDeclaredFields()));

            }

        }
        jedisPipeline.sync();
        jedisPipeline.close();
        cacheResource.close();
        return post;
    }

    @Override
    public void deletePost(String postId) {

        Jedis cacheResource = jedisPool.getResource();
        cacheResource.select(postDBIndex);
        if(!cacheResource.exists(postId)){
            System.out.println("doesn't exist");
            return;
        }
        Set<String> fields = cacheResource.hgetAll(postId).keySet();
        String [] fieldNames = fields.toArray(new String[cacheResource.hgetAll(postId).keySet().size()]);
        cacheResource.hdel(postId, fieldNames);
        System.out.println("deleted");

    }





}
