package main;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.linkedin.replica.wall.main.Wall;
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
        dbSeed.insertUsers();
        dbSeed.insertPosts();


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
        dbSeed.deleteAllUsers();
        dbSeed.deleteAllPosts();
        Wall.shutdown();
    }
}
