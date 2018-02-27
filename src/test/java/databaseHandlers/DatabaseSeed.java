package databaseHandlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.models.*;

public class DatabaseSeed {
    private static Properties properties;
    private ArangoDB arangoDB;
    private String dbName;
    private String likesCollection;
    private String repliesCollection;
    private String commentsCollection;
    private String postsCollection;
    private String usersCollection;

    public DatabaseSeed() throws FileNotFoundException, IOException, ClassNotFoundException {
        properties = new Properties();
        properties.load(new FileInputStream("db_config"));
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");
        repliesCollection = properties.getProperty("collections.replies.name");
        commentsCollection = properties.getProperty("collections.comments.name");
        postsCollection = properties.getProperty("collections.posts.name");
        usersCollection = properties.getProperty("collections.users.name");


    }

    public void insertPosts() throws IOException, ClassNotFoundException {
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/posts"));
        try{
            arangoDB.db(dbName).createCollection(postsCollection);

        }catch(ArangoDBException ex){
            // check if exception was raised because that database was not created
            if(ex.getErrorNum() == 1228){
                arangoDB.createDatabase(dbName);
                arangoDB.db(dbName).createCollection(postsCollection);
            }else{
                throw ex;
            }
        }
        int counter = 1;
        BaseDocument newDoc;
        ArrayList<String> x =  new ArrayList<String>() ;
        x.add("y");
        for(String text : lines){
            Post post = new Post(counter + "", "2", "3",
                    "4", "5", text, "7",
                    true, true, x, x,
                    x, x, x, x, 7,
                    6);
            newDoc = new BaseDocument();
            newDoc.setKey(post.getPostID());
            newDoc.addAttribute("post", post);

            arangoDB.db(dbName).collection(postsCollection).insertDocument(newDoc);
            System.out.println("New post document insert with key = ");
            BaseDocument retrievedDoc = arangoDB.db(dbName).collection(postsCollection).getDocument(post.getPostID(), BaseDocument.class);
            System.out.println("post: " + retrievedDoc.toString());
            counter ++;
        }
    }

    public void insertComments() throws IOException, ClassNotFoundException {
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/comments"));
        try{
            arangoDB.db(dbName).createCollection(commentsCollection);

        }catch(ArangoDBException ex){
            // check if exception was raised because that database was not created
            if(ex.getErrorNum() == 1228){
                arangoDB.createDatabase(dbName);
                arangoDB.db(dbName).createCollection(commentsCollection);
            }else{
                throw ex;
            }
        }
        int counter = 1;
        BaseDocument newDoc;
        ArrayList<String> x =  new ArrayList<String>() ;
        x.add("y");
        for(String text : lines) {
            Comment comment = new Comment(counter + "", "3", "1", 45, 34, x, x, x, text, "11");
            newDoc = new BaseDocument();
            newDoc.setKey(comment.getCommentId());
            newDoc.addAttribute("comment", comment);
            arangoDB.db(dbName).collection(commentsCollection).insertDocument(newDoc);
            System.out.println("New comment document insert with key = ");
            BaseDocument retrievedDoc = arangoDB.db(dbName).collection(commentsCollection).getDocument(comment.getCommentId(), BaseDocument.class);
            System.out.println("comment: " + retrievedDoc.toString());
            counter++;
        }
    }

    public void insertReplies() throws IOException, ClassNotFoundException {
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/replies"));
        try{
            arangoDB.db(dbName).createCollection(repliesCollection);

        }catch(ArangoDBException ex){
            // check if exception was raised because that database was not created
            if(ex.getErrorNum() == 1228){
                arangoDB.createDatabase(dbName);
                arangoDB.db(dbName).createCollection(repliesCollection);
            }else{
                throw ex;
            }
        }
        int counter = 1;
        BaseDocument newDoc;
        ArrayList<String> x =  new ArrayList<String>() ;
        x.add("y");
        Date date = new Date();
        for(String text : lines) {
            Reply reply = new Reply(counter + "", "3", "1", "45", x, 45L, text, date, x, x);
            newDoc = new BaseDocument();
            newDoc.setKey(reply.getReplyId());
            newDoc.addAttribute("reply", reply);
            arangoDB.db(dbName).collection(repliesCollection).insertDocument(newDoc);
            System.out.println("New Reply document insert with key = ");
            BaseDocument retrievedDoc = arangoDB.db(dbName).collection(repliesCollection).getDocument(reply.getReplyId(), BaseDocument.class);
            System.out.println("reply: " + retrievedDoc.toString());
            counter++;
        }
    }

    public void insertLikes() throws IOException, ClassNotFoundException {
        try{
            arangoDB.db(dbName).createCollection(likesCollection);

        }catch(ArangoDBException ex){
            // check if exception was raised because that database was not created
            if(ex.getErrorNum() == 1228){
                arangoDB.createDatabase(dbName);
                arangoDB.db(dbName).createCollection(likesCollection);
            }else{
                throw ex;
            }
        }
        BaseDocument newDoc;
        ArrayList<String> x =  new ArrayList<String>() ;
        x.add("y");
        for(int i = 1 ; i<11; i++) {
            Like like = new Like(i + "", "3", "1", "45", "8", "Yara", "Yara, Safa and 3 others", "url");
            newDoc = new BaseDocument();
            newDoc.setKey(like.getLikeId());
            newDoc.addAttribute("like", like);
            arangoDB.db(dbName).collection(likesCollection).insertDocument(newDoc);
            System.out.println("New like document insert with key = ");
            BaseDocument retrievedDoc = arangoDB.db(dbName).collection(likesCollection).getDocument(like.getLikeId(), BaseDocument.class);
            System.out.println("likes: " + retrievedDoc.toString());
        }
    }

    public void insertUsers() throws IOException, ClassNotFoundException {
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/users"));
        try{
            arangoDB.db(dbName).createCollection(usersCollection);

        }catch(ArangoDBException ex){
            // check if exception was raised because that database was not created
            if(ex.getErrorNum() == 1228){
                arangoDB.createDatabase(dbName);
            }else{
                throw ex;
            }
        }
        int counter = 1;
        BaseDocument newDoc;
        String[] arr;
        for(String text : lines){
            arr = text.split(" ");
            newDoc = new BaseDocument();
            String firstName = arr[0];
            String email = firstName + "@gmail.com";
            String lastName = arr[1];
            UserProfile user = new UserProfile(counter + "", email, firstName, lastName);
            newDoc.setKey(user.getUserId());
            newDoc.addAttribute("user", user);
            arangoDB.db(dbName).collection(usersCollection).insertDocument(newDoc);
            System.out.println("New user document insert with key = " + newDoc.getId());
            counter++;
            BaseDocument retrievedDoc = arangoDB.db(dbName).collection(usersCollection).getDocument(user.getUserId(), BaseDocument.class);
            System.out.println("user: " + retrievedDoc.toString());
        }
    }

    public void deleteAllPosts() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(postsCollection).drop();
        System.out.println("Post collection is dropped");
    }

    public void deleteAllComments() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(commentsCollection).drop();
        System.out.println("Comments collection is dropped");
    }


    public void deleteAllReplies() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(repliesCollection).drop();
        System.out.println("Replies collection is dropped");
    }

    public void deleteAllLikes() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(likesCollection).drop();
        System.out.println("Likes collection is dropped");
    }

    public void deleteAllUsers() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(usersCollection).drop();
        System.out.println("Users collection is dropped");
    }

    public void closeDBConnection() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().shutdown();
    }


//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        DatabaseSeed db = new DatabaseSeed();
//        db.deleteAllPosts();
//        db.deleteAllUsers();
//        db.deleteAllComments();
//        db.deleteAllLikes();
//        db.deleteAllReplies();
//        try {
//            db.insertPosts();
//            db.insertUsers();
//            db.insertComments();
//            db.insertLikes();
//            db.insertReplies();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
}