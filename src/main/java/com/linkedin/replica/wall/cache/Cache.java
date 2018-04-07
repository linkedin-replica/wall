package com.linkedin.replica.wall.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Cache {
    private JedisPool redis;
    private  String REDIS_IP;
    private  int REDIS_PORT;
    private Properties properties;
    private static Cache cache;

    private Cache() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/cache.config"));
        REDIS_IP = properties.getProperty("redis.ip");
        REDIS_PORT = Integer.parseInt(properties.getProperty("redis.port"));
        redis = new JedisPool(new JedisPoolConfig(),REDIS_IP,REDIS_PORT);
    }

    public static void init() throws IOException {
        cache = new Cache();
    }

    public static Cache getInstance(){
        return cache;
    }

    public JedisPool getRedisPool(){
        return redis;
    }
    public void destroyRedisPool(){
        redis.destroy();
    }

}
