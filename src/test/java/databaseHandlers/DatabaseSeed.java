package databaseHandlers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.database.DatabaseConnection;
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
    static Configuration config;
    private ArrayList<UserProfile> insertedUsers;
    private ArrayList<Post> insertedPosts;
    private ArrayList<Comment> insertedComments;
    private ArrayList<Reply> insertedReplies;

    public DatabaseSeed() throws IOException, ClassNotFoundException {
        String rootFolder = "src/main/resources/";
        Configuration.init(rootFolder + "app.config",
                rootFolder + "arango.test.config",
                rootFolder + "commands.config", rootFolder + "controller.config",rootFolder +"cache.config", rootFolder +"query.config");
        config = Configuration.getInstance();
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        dbName = Configuration.getInstance().getArangoConfig("arangodb.name");
        usersCollection = Configuration.getInstance().getArangoConfig("collections.users.name");
        commentsCollection = Configuration.getInstance().getArangoConfig("collections.comments.name");
        repliesCollection = Configuration.getInstance().getArangoConfig("collections.replies.name");
        postsCollection = Configuration.getInstance().getArangoConfig("collections.posts.name");
        insertedUsers = new ArrayList<>();
        insertedPosts = new ArrayList<>();
        insertedComments = new ArrayList<>();
        insertedReplies = new ArrayList<>();

    }

    public void setFriendsAndTheirPosts(){
        ArrayList<String> friends = new ArrayList<String>();
        friends.add(this.getInsertedUsers().get(0).getUserId());
        friends.add(this.getInsertedUsers().get(1).getUserId());
        this.getInsertedUsers().get(5).setFriendsList(friends);
        this.getInsertedPosts().get(0).setAuthorId(this.getInsertedUsers().get(0).getUserId());
        this.getInsertedPosts().get(1).setAuthorId(this.getInsertedUsers().get(0).getUserId());
        this.getInsertedPosts().get(2).setAuthorId(this.getInsertedUsers().get(1).getUserId());
        this.getInsertedPosts().get(3).setAuthorId(this.getInsertedUsers().get(1).getUserId());
        this.getInsertedPosts().get(0).setText("Post 1");
        this.getInsertedPosts().get(1).setText("Post 2");
        this.getInsertedPosts().get(2).setText("Post 3");
        this.getInsertedPosts().get(3).setText("Post 4");

        arangoDB.db(dbName).collection(usersCollection).updateDocument(this.getInsertedUsers().get(5).getUserId(),this.getInsertedUsers().get(5));
        arangoDB.db(dbName).collection(postsCollection).updateDocument(this.getInsertedPosts().get(0).getPostId(),this.getInsertedPosts().get(0));
        arangoDB.db(dbName).collection(postsCollection).updateDocument(this.getInsertedPosts().get(1).getPostId(),this.getInsertedPosts().get(1));
        arangoDB.db(dbName).collection(postsCollection).updateDocument(this.getInsertedPosts().get(2).getPostId(),this.getInsertedPosts().get(2));
        arangoDB.db(dbName).collection(postsCollection).updateDocument(this.getInsertedPosts().get(3).getPostId(),this.getInsertedPosts().get(3));

    }

    public void insertPosts() throws IOException, ClassNotFoundException, ParseException {
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
        for(String text : lines){
            ArrayList<String> images = new ArrayList<String>();
            images.add("images");
            ArrayList<String> videos = new ArrayList<String>();
            videos.add("videos");
            ArrayList<String> likers = new ArrayList<String>();

            Post post = new Post();
            post.setArticle(false);
            post.setAuthorId("2");
            post.setCommentsCount(12);
            post.setImages(images);
            post.setVideos(videos);
            post.setText(text);
            post.setTimestamp(System.currentTimeMillis());
            post.setLikers(likers);
            arangoDB.db(dbName).collection(postsCollection).insertDocument(post);
            insertedPosts.add(post);
            Post retrievedDoc = arangoDB.db(dbName).collection(postsCollection).getDocument(post.getPostId(), Post.class);
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
       ArrayList<String> x =  new ArrayList<String>() ;
       x.add("y");
       for(String text : lines) {
           Comment comment = new Comment();
           comment.setAuthorId("3");
           comment.setParentPostId(insertedPosts.get(0).getPostId());
           comment.setLikesCount(12);
           comment.setRepliesCount(22);
           comment.setText(text);
           comment.setLikers(new ArrayList<String>());
           comment.setTimestamp(System.currentTimeMillis());
           arangoDB.db(dbName).collection(commentsCollection).insertDocument(comment);
           insertedComments.add(comment);
       }
   }


    public void insertReplies() throws IOException {
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
        ArrayList<String> x =  new ArrayList<String>() ;
        x.add("y");
        Date date = new Date();
        for(String text : lines) {
            Reply reply = new Reply();
            reply.setAuthorId("3");
            reply.setParentPostId(insertedPosts.get(0).getPostId());
            reply.setParentCommentId(insertedComments.get(0).getCommentId());
            reply.setLikesCount(4500);
            reply.setText("You are so cute");
            reply.setTimestamp(System.currentTimeMillis());
            reply.setLikers(new ArrayList<String>());
            arangoDB.db(dbName).collection(repliesCollection).insertDocument(reply);
            insertedReplies.add(reply);
        }
    }

     /* seed collection Users in db with dummy data.
     * @throws IOException
     */
    public void insertUsers() throws IOException {
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
        String[] arr;
        for(String text : lines){
            arr = text.split(" ");
            String firstName = arr[0];
            String email = firstName + "@gmail.com";
            String lastName = arr[1];

            UserProfile user = new UserProfile();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setFriendsList(new ArrayList<String>());
            user.setFollowedCompaniesList(new ArrayList<String>());
            arangoDB.db(dbName).collection(usersCollection).insertDocument(user);

            ArrayList<String> b = new ArrayList<>();
            b.add(insertedPosts.get(0).getPostId());
            user.setBookmarks(b);
            arangoDB.db(dbName).collection(usersCollection).updateDocument(user.getUserId(), user);
            insertedUsers.add(user);
            UserProfile retrievedUser= arangoDB.db(dbName).collection(usersCollection).getDocument(user.getUserId(), UserProfile.class);
        }
    }

    /**
     * return list of inserted users.
     * @return
     */
    public ArrayList<UserProfile> getInsertedUsers(){
        return  this.insertedUsers;
    }

    /**
     * return list of inserted comments.
     * @return
     */
    public ArrayList<Comment> getInsertedComments() {
        return insertedComments;
    }

    /**
     * return list of inserted posts.
     * @return
     */
    public ArrayList<Post> getInsertedPosts() {
        return insertedPosts;
    }

    /**
     * return list of inserted likes.
     * @return
     */

    /**
     * return list of inserted replies.
     * @return
     */
    public ArrayList<Reply> getInsertedReplies() {
        return insertedReplies;
    }

    public void deleteAllPosts() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(postsCollection).drop();
    }

    public void deleteAllComments() throws ArangoDBException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(commentsCollection).drop();
    }


    public void deleteAllReplies() throws ArangoDBException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(repliesCollection).drop();
    }


    public void deleteAllUsers() throws ArangoDBException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(usersCollection).drop();
    }

    public void closeDBConnection() throws ArangoDBException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().shutdown();
    }

}