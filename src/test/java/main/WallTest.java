package main;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.linkedin.replica.wall.main.Wall;
import com.linkedin.replica.wall.models.Reply;
import com.linkedin.replica.wall.services.WallService;
import databaseHandlers.DatabaseSeed;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.ArangoDBException;


public class WallTest {
    private static DatabaseSeed dbSeed;
    private static WallService service;

    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, SQLException{
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        Wall.start(args);
        service = new WallService();

        dbSeed = new DatabaseSeed();
//        dbSeed.insertUsers();
//        dbSeed.insertPosts();


    }

    @Test
    public void testAddReplyService() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String, String>();
        request.put("authorId","3");
        request.put("parentPostId","1");
        request.put("parentCommentId","45");
        request.put("mentions","y");
        request.put("likesCount","45");
        request.put("text","TestTestTest");
        request.put("timestamp","Thu Jan 19 2012 01:00 PM");
        request.put("images","y");
        request.put("urls","y");
        service.serve("addReply",request);

        LinkedHashMap<String, Object> result = service.serve("getReplies", request);
        List<Reply> replies = (List<Reply>) result.get("response");
        Boolean found = false;
        for(int i =0;i<replies.size();i++){
            if(replies.get(i).getText().equals("TestTestTest")){
                found = true;
                break;
            }
        }
        assertEquals("Texts should be the same",found,true);

    }

    @Test
    public void testEditReply() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String, String>();
        request.put("replyId","1");
        request.put("authorId","3");
        request.put("parentPostId","1");
        request.put("parentCommentId","45");
        request.put("mentions","y");
        request.put("likesCount","45");
        request.put("text","Testing service edit");
        request.put("timestamp","Thu Jan 19 2012 01:00 PM");
        request.put("images","y");
        request.put("urls","y");
        service.serve("editReply",request);
        LinkedHashMap<String, Object> result = service.serve("getReplies", request);
        List<Reply> replies = (List<Reply>) result.get("response");
        Boolean found = false;
        for(int i =0;i<replies.size();i++){
            if(replies.get(i).getText().equals("Testing service edit") && replies.get(i).getReplyId().equals("1")){
                found = true;
                break;
            }
        }
        assertEquals("Texts should be the same",found,true);
    }

    @Test
    public void testDeleteReply() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {

        HashMap<String,String> request = new HashMap<String, String>();
        request.put("replyId","1");
        request.put("authorId","3");
        request.put("parentPostId","1");
        request.put("parentCommentId","45");
        request.put("mentions","y");
        request.put("likesCount","45");
        request.put("text","Testing");
        request.put("timestamp","Thu Jan 19 2012 01:00 PM");
        request.put("images","y");
        request.put("urls","y");

        LinkedHashMap<String, Object> result = service.serve("getReplies", request);
        List<Reply> replies = (List<Reply>) result.get("response");

        service.serve("deleteReply",request);

        LinkedHashMap<String, Object> testResult = service.serve("getReplies", request);
        List<Reply> testReplies = (List<Reply>) testResult.get("response");

        assertEquals("Size should decrement by one",replies.size()-1,testReplies.size());
    }
//    @Test
//    public void testSearchUsers() throws FileNotFoundException, ClassNotFoundException, IOException, SQLException, InstantiationException, IllegalAccessException{
//        System.out.println("er");
//        String searchKey = "hm";
//        HashMap<String,String> htbl =  new HashMap<String, String>();
//        htbl.put("searchKey", searchKey);
//        List<User> results = (List<User>) service.serve("search.user",htbl).get("results");
//
//        boolean check = false;
//        for(User user : results){
//            if(user.getFirstName().contains(searchKey))
//                check = true;
//
//            if(user.getLastName().contains(searchKey))
//                check = true;
//
//            assertEquals("Wrong Fetched User as his/her firstName and lastName does not match search key.", true, check);
//            check = false;
//        }
//    }

//    @Test
//    public void testSearchCompanies() throws FileNotFoundException, ClassNotFoundException, IOException, SQLException, InstantiationException, IllegalAccessException{
//        String searchKey = "Goo";
//        HashMap<String,String> htbl =  new HashMap<String, String>();
//        htbl.put("searchKey", searchKey);
//        List<Company> results = (List<Company>) service.serve("search.company",htbl).get("results");
//
//        boolean check = false;
//        for(Company company : results){
//            if(company.getCompanyName().contains(searchKey))
//                check = true;
//
//            assertEquals("Wrong Fetched Company as its company name does not match search key.", true, check);
//            check = false;
//        }
//    }

//    @Test
//    public void testSearchPosts() throws FileNotFoundException, ClassNotFoundException, IOException, SQLException, InstantiationException, IllegalAccessException{
//        String searchKey = "Lorem";
//        HashMap<String,String> htbl =  new HashMap<String, String>();
//        htbl.put("searchKey", searchKey);
//        List<Post> results = (List<Post>) service.serve("search.post",htbl).get("results");
//
//        searchKey = searchKey.toLowerCase();
//        boolean check = false;
//        for(Post post : results){
//
//            if(post.getText().toLowerCase().contains(searchKey))
//                check = true;
//
//            assertEquals("Wrong Fetched post as its text does not match search key.", true, check);
//            check = false;
//        }
//    }

    @AfterClass
    public static void tearDown() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException, SQLException{
//        dbSeed.deleteAllUsers();
//        dbSeed.deleteAllPosts();
        Wall.shutdown();
    }
}
