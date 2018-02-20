package database;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


public interface DatabaseHandler {

    /**
     * Initiate a connection with the database
     */
     void connect();

    /**
     * Close a connection with the database
     */
    void disconnect();
}
