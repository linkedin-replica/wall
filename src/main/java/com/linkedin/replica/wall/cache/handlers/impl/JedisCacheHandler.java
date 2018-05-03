package com.linkedin.replica.wall.cache.handlers.impl;

import com.google.gson.Gson;
import com.linkedin.replica.wall.cache.Cache;
import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.models.ReturnedPost;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;


import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

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
    public void cachePost(String postId, Object post) throws IOException, IllegalAccessException{

        try(Jedis cacheResource = jedisPool.getResource()){
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
            e.printStackTrace();
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
           e.printStackTrace();
        }


    }

    @Override
    public void editPost(String postId, HashMap<String, Object> hm) throws IOException { ;
        try(Jedis cacheResource = jedisPool.getResource()){
            cacheResource.select(postDBIndex);
            if(!cacheResource.exists(postId)){
                return;
            }
            Pipeline jedisPipeline = cacheResource.pipelined();
            for (String key : hm.keySet())
            {
                jedisPipeline.hset(postId,key,gson.toJson(hm.get(key)));

            }
            jedisPipeline.sync();
            jedisPipeline.close();
        }
        catch (JedisException e){
            e.printStackTrace();
        }

    }

    @Override
    public Object getCompanyPosts(String companyId, int limit,Class<?> postClass)  {
       TreeSet<ReturnedPost> returnedPosts = new TreeSet<>();
        try(Jedis cacheResource = jedisPool.getResource()){
            cacheResource.select(postDBIndex);
            if(!cacheResource.exists(companyId)){
                return null;
            }
            if(cacheResource.hlen(companyId)<limit){
                return null;
            }

            Map<String,String> posts = cacheResource.hgetAll(companyId);
            for (Map.Entry<String, String> value : posts.entrySet())
            {
               returnedPosts.add(gson.fromJson(value.getValue(), (Type) postClass));
            }
     }

        return new ArrayList<>(returnedPosts);
    }

    @Override
    public void cacheCompanyPosts(String companyId,  List<ReturnedPost> returnedPosts) throws IOException {
        try(Jedis cacheResource = jedisPool.getResource()){
            Pipeline jedisPipeline = cacheResource.pipelined();
            cacheResource.select(postDBIndex);
            for (int i = 0 ; i <returnedPosts.size();i++){
                jedisPipeline.hset(companyId,"post" + returnedPosts.get(i).getPostId(), gson.toJson(returnedPosts.get(i)));
            }
            jedisPipeline.sync();
            jedisPipeline.close();
        }
        catch (JedisException e){
            e.printStackTrace();
        }

    }

    @Override
    public void deleteCompanyPosts(String companyId, String postId) throws IOException {
        try(Jedis cacheResource = jedisPool.getResource()){
            cacheResource.select(postDBIndex);
            cacheResource.hdel(companyId, String.format("post%s", postId));

        }
        catch (JedisException e){
            e.printStackTrace();
        }

    }
}
