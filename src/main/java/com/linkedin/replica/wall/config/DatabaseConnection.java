package com.linkedin.replica.wall.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.arangodb.ArangoDBException;
import com.arangodb.entity.CollectionEntity;
import redis.clients.jedis.Jedis;

import com.arangodb.ArangoDB;


public class DatabaseConnection {
    private ArangoDB arangoDB;
    private Jedis redis;

    private static DatabaseConnection instance;
    private Properties properties;

    private DatabaseConnection() throws FileNotFoundException, IOException, ClassNotFoundException{
        properties = new Properties();
        properties.load(new FileInputStream("db_config"));

        arangoDB = instantiateArrangoDB();
        redis = new Jedis();
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
    public static DatabaseConnection getInstance() throws FileNotFoundException, IOException, ClassNotFoundException{
        if(instance == null){
            synchronized (DatabaseConnection.class) {
                if(instance == null){
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
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
    private ArangoDB instantiateArrangoDB(){
        return new ArangoDB.Builder()
                .user(properties.getProperty("arangodb.user"))
                .password(properties.getProperty("arangodb.password"))
                .build();
    }

    /**
     * Creates the wall db
     */
    private void createDatabase() {
        String dbName = properties.getProperty("arangodb.name");
        try {
            arangoDB.createDatabase(dbName);
            System.out.println("Database created: " + dbName);
        } catch (ArangoDBException e) {
            System.err.println("Failed to create database: " + dbName + "; " + e.getMessage());
        }
    }

    /**
     * create collection with name of corresponding name.
     */
    private void createCollection(String name) {
        String dbName = properties.getProperty("arangodb.name");
            try {
                CollectionEntity myArangoCollection = arangoDB.db(dbName).createCollection(name);
                System.out.println("Collection created: " + myArangoCollection.getName());
            } catch (ArangoDBException e) {
                System.err.println("Failed to create collection: " + name + "; " + e.getMessage());
            }
        }

    /**
     * Creates the posts, comments, replies and likes collections
     */
    private void createAllCollections(){
        createCollection(properties.getProperty("collections.posts.name"));
        createCollection(properties.getProperty("collections.comments.name"));
        createCollection(properties.getProperty("collections.replies.name"));
        createCollection(properties.getProperty("collections.users.name"));

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
