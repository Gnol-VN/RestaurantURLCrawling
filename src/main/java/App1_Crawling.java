
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.File;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class App1_Crawling {
    private static String INPUT_FILE_URL =  "shard5-website.csv";
    private static String COLLECTION_NAME = "restaurantAnalytics5";

    private static MongoClientURI uri = new MongoClientURI(
             "mongodb+srv://admin:admin@learningcluster-mntmv.mongodb.net/test?retryWrites=true&w=majority");
    private static CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
             fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    private static MongoClient mongoClient = new MongoClient(uri);
    private static MongoDatabase database = mongoClient.getDatabase("toast").withCodecRegistry(pojoCodecRegistry);
    private static MongoCollection<Restaurant> dbCollection = database.getCollection(COLLECTION_NAME, Restaurant.class);

    private static List<Restaurant> RESTAURANT_LIST = new ArrayList<>();
    static List<Restaurant> FAILED_LIST = new ArrayList<>();
    public static void main(String[] args)   throws Exception{
        restaurantSetUp();

        //Divide the restaurant list into half
        List<Restaurant> list1 = RESTAURANT_LIST.subList(0, RESTAURANT_LIST.size()/2);
        List<Restaurant> list2 = RESTAURANT_LIST.subList(RESTAURANT_LIST.size()/2, RESTAURANT_LIST.size());


        MyRunnable myRunnable = new MyRunnable(list1);
        MyRunnable myRunnable2 = new MyRunnable(list2);
        Thread thread1 = new Thread(myRunnable);
        Thread thread2 = new Thread(myRunnable2);
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Failed List");
        for(Restaurant restaurant : FAILED_LIST){
            System.out.println(restaurant.getRestaurantName() + ",," +restaurant.getRestaurantURL());
        }

    }

    static void restaurantSetUp() throws Exception {
        Reader reader = Files.newBufferedReader(Paths.get(INPUT_FILE_URL));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

        //Create a restaurant map from new FEATURE file
        for (CSVRecord csvRecord : csvParser) {
            // Accessing Values by Column Index
            Restaurant restaurant = new Restaurant();
            restaurant.setRestaurantName(csvRecord.get(0));
            restaurant.setRestaurantURL(csvRecord.get(2).toLowerCase().trim());

            RESTAURANT_LIST.add(restaurant);
        }
        Set<Restaurant> set = new LinkedHashSet<>();
        set.addAll(RESTAURANT_LIST);
        RESTAURANT_LIST.clear();
        RESTAURANT_LIST.addAll(set);    }


    static void persist(Restaurant restaurant) throws ClassNotFoundException {
        dbCollection.insertOne(restaurant);
    }
}
