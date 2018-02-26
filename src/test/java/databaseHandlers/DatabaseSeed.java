package databaseHandlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.linkedin.replica.wall.config.DatabaseConnection;

public class DatabaseSeed {
    private static Properties properties;

    public DatabaseSeed() throws FileNotFoundException, IOException{
        properties = new Properties();
        properties.load(new FileInputStream("db_config"));
    }

    public void insertPosts() throws IOException, ClassNotFoundException, SQLException{
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/posts"));
        ArangoDB arangoDB = DatabaseConnection.getInstance().getArangodb();
        String dbName = properties.getProperty("arangodb.name");
        String collectionName = properties.getProperty("collection.posts.name");

        try{
            arangoDB.db(dbName).createCollection(collectionName);

        }catch(ArangoDBException ex){
            // check if exception was raised because that database was not created
            if(ex.getErrorNum() == 1228){
                arangoDB.createDatabase(dbName);
                arangoDB.db(dbName).createCollection(collectionName);
            }else{
                throw ex;
            }
        }
        int counter = 0;
        BaseDocument newDoc;

        for(String text : lines){
            newDoc = new BaseDocument();
            newDoc.addAttribute("authorId", counter+"");
            newDoc.addAttribute("text", text);
            System.out.println("fdsgffdsgdf");
            arangoDB.db(dbName).collection(collectionName).insertDocument(newDoc);
            System.out.println("New user document insert with key = " + newDoc.getId());
            counter++;
        }
    }

    public void insertUsers() throws IOException, ClassNotFoundException, SQLException{
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/users"));
        ArangoDB arangoDB = DatabaseConnection.getInstance().getArangodb();
        String dbName = properties.getProperty("arangodb.name");
        String collectionName = properties.getProperty("collection.users.name");

        try{
            arangoDB.db(dbName).createCollection(collectionName);

        }catch(ArangoDBException ex){
            // check if exception was raised because that database was not created
            if(ex.getErrorNum() == 1228){
                arangoDB.createDatabase(dbName);
            }else{
                throw ex;
            }
        }
        int counter = 0;
        BaseDocument newDoc;
        String[] arr;
        for(String text : lines){
            arr = text.split(" ");
            newDoc = new BaseDocument();
            newDoc.addAttribute("userID", counter+"");
            newDoc.addAttribute("firstName", arr[0]);
            newDoc.addAttribute("lastName", arr[1]);
            arangoDB.db(dbName).collection(collectionName).insertDocument(newDoc);
            System.out.println("New user document insert with key = " + newDoc.getId());
            counter++;
        }
    }

    public void deleteAllPosts() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException, SQLException{
        String dbName = properties.getProperty("arangodb.name");
        String collectionName = properties.getProperty("collection.posts.name");
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(collectionName).drop();
        System.out.println("Post collection is dropped");
    }


    public void deleteAllUsers() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException, SQLException{
        String dbName = properties.getProperty("arangodb.name");
        String collectionName = properties.getProperty("collection.users.name");
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(collectionName).drop();
        System.out.println("Users collection is dropped");
    }

    public void closeDBConnection() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException, SQLException{
        DatabaseConnection.getInstance().getArangodb().shutdown();
    }

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        DatabaseSeed db = new DatabaseSeed();
        try {
            db.insertPosts();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}