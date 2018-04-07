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
    private ArrayList<Like> insertedLikes;
    private ArrayList<Reply> insertedReplies;

    public DatabaseSeed() throws IOException, ClassNotFoundException {
        String rootFolder = "src/main/resources/";
        Configuration.init(rootFolder + "app.config",
                rootFolder + "arango.test.config",
                rootFolder + "commands.config", rootFolder + "controller.config");
        config = Configuration.getInstance();
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        dbName = Configuration.getInstance().getArangoConfig("arangodb.name");
        likesCollection = Configuration.getInstance().getArangoConfig("collections.likes.name");
        usersCollection = Configuration.getInstance().getArangoConfig("collections.users.name");
        commentsCollection = Configuration.getInstance().getArangoConfig("collections.comments.name");
        repliesCollection = Configuration.getInstance().getArangoConfig("collections.replies.name");
        postsCollection = Configuration.getInstance().getArangoConfig("collections.posts.name");
        insertedUsers = new ArrayList<>();
        insertedPosts = new ArrayList<>();
        insertedComments = new ArrayList<>();
        insertedLikes = new ArrayList<>();
        insertedReplies = new ArrayList<>();

    }

    public void setFriendsAndTheirPosts(){
        ArrayList<String> friends = new ArrayList<String>();
        friends.add(this.getInsertedUsers().get(0).getUserId());
        friends.add(this.getInsertedUsers().get(1).getUserId());
        System.out.println(friends.toString() + " Friends in  WallTest");
        this.getInsertedUsers().get(5).setFriendsList(friends);
        this.getInsertedPosts().get(0).setAuthorId(this.getInsertedUsers().get(0).getUserId());
        this.getInsertedPosts().get(1).setAuthorId(this.getInsertedUsers().get(0).getUserId());
        this.getInsertedPosts().get(2).setAuthorId(this.getInsertedUsers().get(1).getUserId());
        this.getInsertedPosts().get(3).setAuthorId(this.getInsertedUsers().get(1).getUserId());
        this.getInsertedPosts().get(0).setTimestamp(new Date(2010,6,24));
        this.getInsertedPosts().get(1).setTimestamp(new Date(2009,8,10));
        this.getInsertedPosts().get(2).setTimestamp(new Date(2016,9,6));
        this.getInsertedPosts().get(3).setTimestamp(new Date(2018,2,22));
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
        int counter = 1;
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Date timestamp = format.parse("Mon Mar 19 2012 01:00 PM");
        for(String text : lines){
            ArrayList<String> images = new ArrayList<String>();
            images.add("images");
            ArrayList<String> videos = new ArrayList<String>();
            videos.add("videos");
            ArrayList<String> urls = new ArrayList<String>();
            urls.add("urls");
            ArrayList<String> hashtags = new ArrayList<String>();
            hashtags.add("hashtags");
            ArrayList<String> mentions = new ArrayList<String>();
            mentions.add("mentions");
            ArrayList<String> shares = new ArrayList<String>();
            mentions.add("shares");

            Post post = new Post( "2", "3",
                    "4", "5", text,hashtags,
                    mentions, 12, images, videos,
                    urls, 30, timestamp, true,
                    true, shares,"headLine", false);



            arangoDB.db(dbName).collection(postsCollection).insertDocument(post);
            insertedPosts.add(post);
            Post retrievedDoc = arangoDB.db(dbName).collection(postsCollection).getDocument(post.getPostId(), Post.class);
            System.out.println(retrievedDoc);
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
       ArrayList<String> x =  new ArrayList<String>() ;
       x.add("y");
       for(String text : lines) {
           Comment comment = new Comment("3", insertedPosts.get(0).getPostId(), 45, 34, x, x, x, text, null);
           arangoDB.db(dbName).collection(commentsCollection).insertDocument(comment);
           insertedComments.add(comment);
           Comment retrievedDoc = arangoDB.db(dbName).collection(commentsCollection).getDocument(comment.getCommentId(), Comment.class);
           counter++;
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
        int counter = 1;
        ArrayList<String> x =  new ArrayList<String>() ;
        x.add("y");
        Date date = new Date();
        for(String text : lines) {
            Reply reply = new Reply("3", insertedPosts.get(0).getPostId(), insertedComments.get(0).getCommentId(), x, 4500, text, date, x, x);
            arangoDB.db(dbName).collection(repliesCollection).insertDocument(reply);
            insertedReplies.add(reply);
            BaseDocument retrievedDoc = arangoDB.db(dbName).collection(repliesCollection).getDocument(reply.getReplyId(), BaseDocument.class);
            counter++;
        }
    }

    public void insertLikes() {
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
        String [] userNames = new String [] {"Mohammed", "Nada", "Rana", "Safa", "Yara"};
        Random rand;
        for(int i = 0 ; i<10; i++) {
            rand = new Random();
            String likerId = "12";
            String likedPostId = null;
            String likedCommentId = null;
            String likedReplyId = null;
            if(i%3 == 0){
                likedPostId = insertedPosts.get(0).getPostId();
            } else if (i%3 == 1) {
                likedCommentId = insertedComments.get(0).getCommentId();
            } else {
                likedReplyId = insertedReplies.get(0).getReplyId();
            }
            String userName = userNames[rand.nextInt(5)];
            String headLine = userNames[rand.nextInt(5)] + ", " + userNames[rand.nextInt(5)] + " and 2 others";
            String imageUrl = "url" + i;
            Like like = new Like( likerId, likedPostId, likedCommentId, likedReplyId, userName, headLine, imageUrl);
            DocumentCreateEntity likeDoc = arangoDB.db(dbName).collection(likesCollection).insertDocument(like);
            insertedLikes.add(like);
            System.out.println("New like document insert with key = "  + likeDoc.getKey());
        }
    }

    /**
     * seed collection Users in db with dummy data.
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
        int counter = 1;
        String[] arr;
        for(String text : lines){
            arr = text.split(" ");
            String firstName = arr[0];
            String email = firstName + "@gmail.com";
            String lastName = arr[1];

            UserProfile user = new UserProfile(email, firstName, lastName);
            arangoDB.db(dbName).collection(usersCollection).insertDocument(user);

            Bookmark bookmark = new Bookmark(user.getUserId(), insertedPosts.get(0).getPostId());
            ArrayList<Bookmark> b = new ArrayList<>();
            b.add(bookmark);
            user.setBookmarks(b);
            arangoDB.db(dbName).collection(usersCollection).updateDocument(user.getUserId(), user);
            insertedUsers.add(user);
            System.out.println("New user document insert with key = " + user.getUserId());
            counter++;
            UserProfile retrievedUser= arangoDB.db(dbName).collection(usersCollection).getDocument(user.getUserId(), UserProfile.class);
            System.out.println("user: " + retrievedUser.toString());
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
    public ArrayList<Like> getInsertedLikes() {
        return insertedLikes;
    }

    /**
     * return list of inserted replies.
     * @return
     */
    public ArrayList<Reply> getInsertedReplies() {
        return insertedReplies;
    }

    public void deleteAllPosts() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(postsCollection).drop();
        System.out.println("Post collection is dropped");
    }

    public void deleteAllComments() throws ArangoDBException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(commentsCollection).drop();
        System.out.println("Comments collection is dropped");
    }


    public void deleteAllReplies() throws ArangoDBException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(repliesCollection).drop();
        System.out.println("Replies collection is dropped");
    }

    public void deleteAllLikes() throws ArangoDBException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(likesCollection).drop();
        System.out.println("Likes collection is dropped");
    }

    public void deleteAllUsers() throws ArangoDBException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().db(dbName).collection(usersCollection).drop();
        System.out.println("Users collection is dropped");
    }

    public void closeDBConnection() throws ArangoDBException, ClassNotFoundException, IOException {
        DatabaseConnection.getInstance().getArangodb().shutdown();
    }

}