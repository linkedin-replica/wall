package com.linkedin.replica.wall.main;

import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.config.DatabaseConnection;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Wall {

    public static void start(String... args) throws FileNotFoundException, ClassNotFoundException, IOException {
        if(args.length != 2)
            throw new IllegalArgumentException("Expected two arguments. 1-database_config file path "
                    + "2- command_config file path");

        // create singleton instance of Configuration class that will hold configuration files paths
        Configuration.getInstance(args[0], args[1]);

        // create singleton instance of DatabaseConnection class that is responsible for initiating connections
        // with databases
        DatabaseConnection.getInstance();
    }

    public static void shutdown() throws FileNotFoundException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().closeConnections();
    }

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
        Wall.start(args);
    }
}
