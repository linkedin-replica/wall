package com.linkedin.replica.wall.main;

import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.config.DatabaseConnection;
import messaging.ClientMessagesReceiver;
//import messaging.ClientMessagesReceiver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    private ClientMessagesReceiver clientMessagesReceiver;
    public void start(String... args) throws FileNotFoundException, ClassNotFoundException, IOException, TimeoutException {
        if(args.length != 3)
            throw new IllegalArgumentException("Expected three arguments. 1-database_config file path "
                    + "2- command_config file path"+" 3-message_config file path");

        // create singleton instance of Configuration class that will hold configuration files paths
        Configuration.init(args[0], args[1], args[2]);

        // create singleton instance of DatabaseConnection class that is responsible for initiating connections
        // with databases
        DatabaseConnection.getInstance();
        clientMessagesReceiver = new ClientMessagesReceiver();
    }

    public  void shutdown() throws FileNotFoundException, ClassNotFoundException, IOException, TimeoutException {
        DatabaseConnection.getInstance().closeConnections();
        clientMessagesReceiver.closeConnection();
    }

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, TimeoutException {
        new Main().start(args);
    }

}
