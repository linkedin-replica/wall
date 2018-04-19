package com.linkedin.replica.wall.cache.handlers.impl;

import com.google.gson.Gson;
import com.linkedin.replica.wall.cache.Cache;
import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.models.Post;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
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

        try(Jedis cacheResource = jedisPool.getResource()){
            Pipeline jedisPipeline = cacheResource.pipelined();
            cacheResource.select(postDBIndex);
            Class postClass = post.getClass();
            Field [] postFields = postClass.getDeclaredFields();
            for (int i = 0; i<postFields.length; i++){
                postFields[i].setAccessible(true);
                String fieldName = postFields[i].getName();
                Object value =  postFields[i].get(post);
                System.out.println("field name " + fieldName);
                System.out.println("value  " + value);
                jedisPipeline.hset(postId,fieldName,gson.toJson(value));

            }
            jedisPipeline.sync();
            jedisPipeline.close();
        }
        catch (JedisException e){
          e.printStackTrace();
        }

    }

    @Override
    public Object getPost(String postId, Class<?> postClass) throws IllegalAccessException, InstantiationException, NoSuchFieldException, IOException {

        Object post = postClass.newInstance();
        try(Jedis cacheResource = jedisPool.getResource()){
           cacheResource.select(postDBIndex);
            if(!cacheResource.exists(postId)){
                return null;
            }

            Pipeline jedisPipeline = cacheResource.pipelined();
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
        }
        catch (JedisException e){
            System.out.println(e.getMessage());
        }

        return post;
    }

    @Override
    public void deletePost(String postId) {

        try(Jedis cacheResource = jedisPool.getResource()){
           cacheResource.select(postDBIndex);
            if(!cacheResource.exists(postId)){
                return;
            }
            Set<String> fields = cacheResource.hgetAll(postId).keySet();
            String [] fieldNames = fields.toArray(new String[cacheResource.hgetAll(postId).keySet().size()]);
            cacheResource.hdel(postId, fieldNames);
        }
        catch (JedisException e){
            System.out.println(e.getMessage());
        }


    }

    @Override
    public void editPost(String postId, HashMap<String, Object> hm) throws IOException {
        try(Jedis cacheResource = jedisPool.getResource()){
            if(!cacheResource.exists(postId)){
                return;
            }
            cacheResource.select(postDBIndex);
            Pipeline jedisPipeline = cacheResource.pipelined();
            for (String key : hm.keySet())
            {
                jedisPipeline.hset(postId,key,gson.toJson(hm.get(key)));

            }
            jedisPipeline.sync();
            jedisPipeline.close();
        }
        catch (JedisException e){
            System.out.println(e.getMessage());
        }

    }
}
