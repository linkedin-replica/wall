package database;

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
