package com.linkedin.replica.wall.database;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.arangodb.ArangoDBException;
import com.linkedin.replica.wall.config.Configuration;
import redis.clients.jedis.Jedis;

import com.arangodb.ArangoDB;


public class DatabaseConnection {
    private ArangoDB arangoDB;
    private Jedis redis;
    private Configuration config;
    private static DatabaseConnection instance;


    private DatabaseConnection() {
        config = Configuration.getInstance();
        instantiateArrangoDB();
        //redis = new Jedis();
    }

    /**
     * To reduce use of synchronization, use  double-checked locking.
     * we first check to see if an instance is created, and if not, then we synchronize. This
     * way, we only synchronize the first time which is the initialization phase.
     *
     * @return
     * 		DatabaseConnection singleton instance
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    public static DatabaseConnection getInstance() {
        return instance;
    }

    public static void init() {
        instance = new DatabaseConnection();
    }

    /**
     * Implement the clone() method and throw an exception so that the singleton cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException("DatabaseConnection singleton, cannot be clonned");
    }
    /**
     * Instantiate ArangoDB
     * @return
     */
    private void instantiateArrangoDB(){
        arangoDB =  new ArangoDB.Builder()
                .user(config.getArangoConfig("arangodb.user"))
                .password(config.getArangoConfig("arangodb.password"))
                .build();
    }



    public void closeConnections() {
        if(arangoDB != null)
            arangoDB.shutdown();

        if(redis != null)
            redis.shutdown();

    }

    public ArangoDB getArangodb() {
        return arangoDB;
    }


    public Jedis getRedis() {
        return redis;
    }
    
}
