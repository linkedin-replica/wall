package database;

/**
 * A singleton class carrying a database instance
 */
public class DatabaseInstance {

    private static DatabaseHandler db = new ArangoHandler();

    private DatabaseInstance() {}

    /**
     * Get a singleton DB instance
     * @return The DB instance
     */
    public static DatabaseHandler getInstance() {
        return db;
    }
}
