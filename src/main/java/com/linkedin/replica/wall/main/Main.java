package com.linkedin.replica.wall.main;

import com.linkedin.replica.wall.config.Configuration;
//import com.linkedin.replica.wall.controller.Server;
import com.linkedin.replica.wall.database.DatabaseConnection;
import com.linkedin.replica.wall.messaging.MessageReceiver;
//import messaging.ClientMessagesReceiver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    /**
     * Used for testing when starting netty server is not required
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     */
    public static void testingStart(String... args) throws FileNotFoundException, ClassNotFoundException, IOException {
        // create singleton instance of Configuration class that will hold configuration files paths
        Configuration.init(args[0], args[1], args[2],args[3],args[4]);

        // create singleton instance of DatabaseConnection class that is responsible for intiating connections
        // with databases
        DatabaseConnection.init();
    }

    public static void start(String... args) throws FileNotFoundException, ClassNotFoundException, IOException, InterruptedException, TimeoutException{
        if(args.length != 4)
            throw new IllegalArgumentException("Expected three arguments. 1- app config file path \n "
                    + "2- database config file path \n  3- commands config file path \n 4- controller config file path \n 5- redis config file path");

        // create singleton instance of Configuration class that will hold configuration files paths
        Configuration.init(args[0], args[1], args[2], args[3],args[4]);

        // create singleton instance of DatabaseConnection class that is responsible for intiating connections
        // with databases
        DatabaseConnection.init();
        // start RabbitMQ
        new MessageReceiver();
        // start server
        new Thread(new Runnable() {

            @Override
            public void run() {
//                try {
//                    new Server().start();
//                } catch (InterruptedException e) {
//                    //TODO logging
//                }
            }
        }).start();
    }

    public static void shutdown() throws FileNotFoundException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().closeConnections();
    }

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, InterruptedException, TimeoutException {
        String[] arr = {"src/main/resources/app.config","src/main/resources/arango.test.config", "src/main/resources/commands.config", "src/main/resources/controller.config"};
        start(arr);
    }

}
