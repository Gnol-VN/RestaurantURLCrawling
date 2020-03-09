import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class App2_FailedList {
    static List<String> INPUT_LIST;
    static String INPUT_FILE_URL =  "restaurant2.txt";
    static String DB_COLLECTION =  "restaurantAnalytics4";

    static MongoClientURI uri = new MongoClientURI(
            "mongodb+srv://admin:admin@learningcluster-mntmv.mongodb.net/test?retryWrites=true&w=majority");
    static CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    static MongoClient mongoClient = new MongoClient(uri);
    static MongoDatabase database = mongoClient.getDatabase("toast").withCodecRegistry(pojoCodecRegistry);
    static MongoCollection<Restaurant> dbCollection = database.getCollection(DB_COLLECTION, Restaurant.class);


    public static void main(String[] args) throws IOException {
       listFailedRestaurant(INPUT_FILE_URL, DB_COLLECTION);
    }
    public static List<String> listFailedRestaurant(String inputFileURL, String dbCollectionName){
        INPUT_FILE_URL = inputFileURL;
        DB_COLLECTION = dbCollectionName;
        List<String> failedList = new ArrayList<>();
        List<Restaurant> RestaurantListFromInputFile = new ArrayList<>();
        ArrayList<Restaurant> RestaurantListFromDB = dbCollection.find().into(new ArrayList<>());

        try {
            INPUT_LIST = Files.readAllLines(new File(INPUT_FILE_URL).toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String item: INPUT_LIST){
            Restaurant restaurant = new Restaurant();
            restaurant.setRestaurantName(item.split(",")[0]);
            restaurant.setRestaurantURL(item.split(",")[item.split(",").length-1]);
            RestaurantListFromInputFile.add(restaurant);
        }

        Set<Restaurant> set = new LinkedHashSet<>();
        set.addAll(RestaurantListFromInputFile);
        RestaurantListFromInputFile.clear();
        RestaurantListFromInputFile.addAll(set);

        int i = 0;
        for(Restaurant restaurant_in_input : RestaurantListFromInputFile ){
            if(!RestaurantListFromDB.contains(restaurant_in_input)){
                String failedRestaurantString = restaurant_in_input.getRestaurantName()+",,"+restaurant_in_input.getRestaurantURL();
                failedList.add(failedRestaurantString);
                i++;
            }
        }
        return failedList;
    }
}
