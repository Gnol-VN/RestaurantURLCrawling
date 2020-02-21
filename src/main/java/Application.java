
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class Application {
    static MongoClientURI uri = new MongoClientURI(
            "mongodb+srv://admin:admin@learningcluster-mntmv.mongodb.net/test?retryWrites=true&w=majority");
    static CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    static MongoClient mongoClient = new MongoClient(uri);
    static MongoDatabase database = mongoClient.getDatabase("toast").withCodecRegistry(pojoCodecRegistry);
    static MongoCollection<Restaurant> dbCollection = database.getCollection("restaurantAnalytics2", Restaurant.class);


    static List<String> inputList;
    static List<Restaurant> restaurantList = new ArrayList<>();
    static List<Restaurant> failedList = new ArrayList<>();
    public static void main(String[] args)   throws Exception{
        restaurantSetUp();

        //Divide the restaurant list into half
        List<Restaurant> list1 = restaurantList.subList(0, restaurantList.size()/2);
        List<Restaurant> list2 = restaurantList.subList(restaurantList.size()/2, restaurantList.size());


        MyRunnable myRunnable = new MyRunnable(list1);
        MyRunnable myRunnable2 = new MyRunnable(list2);
        Thread thread1 = new Thread(myRunnable);
        Thread thread2 = new Thread(myRunnable2);
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Failed List");
        for(Restaurant restaurant : failedList){
            System.out.println(restaurant.getRestaurantName() + " " +restaurant.getRestaurantURL());
        }

    }

    static void restaurantSetUp() throws Exception {
        inputList = Files.readAllLines(new File("restaurant.txt").toPath(), Charset.defaultCharset());

        for(String item: inputList){
            Restaurant restaurant = new Restaurant();
            restaurant.setRestaurantName(item.split(",")[0]);
            restaurant.setRestaurantURL(item.split(",")[item.split(",").length-1]);
            restaurantList.add(restaurant);
        }

        Set<Restaurant> set = new LinkedHashSet<>();
        set.addAll(restaurantList);
        restaurantList.clear();
        restaurantList.addAll(set);
    }


    static void persist(Restaurant restaurant) throws ClassNotFoundException {
        dbCollection.insertOne(restaurant);
    }
}
