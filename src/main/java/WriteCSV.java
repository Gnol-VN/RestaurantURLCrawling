import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class WriteCSV {

    private static final String SAMPLE_CSV_FILE = "./shard2.csv";
    static String DB_COLLECTION =  "restaurantAnalytics4";
    static String INPUT_FILE_URL =  "restaurant2.txt";

    static MongoClientURI uri = new MongoClientURI(
            "mongodb+srv://admin:admin@learningcluster-mntmv.mongodb.net/test?retryWrites=true&w=majority");
    static CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    static MongoClient mongoClient = new MongoClient(uri);
    static MongoDatabase database = mongoClient.getDatabase("toast").withCodecRegistry(pojoCodecRegistry);
    static MongoCollection<Restaurant> dbCollection = database.getCollection(DB_COLLECTION, Restaurant.class);

    public static void main(String[] args) throws IOException {
        CSVPrinter csvPrinter = CSV_Setup();


        ArrayList<Restaurant> RestaurantListFromDB = dbCollection.find().into(new ArrayList<>());
        unicifyRestaurant(RestaurantListFromDB);
        for (Restaurant restaurant : RestaurantListFromDB) {
            String onlineOrder = "", giftcards = "", findcard = "", rewards = "", rewardsSignup = "";
            for (String toastURL : restaurant.getToastURLSets()) {
                if (toastURL.contains("/rewardsSignup")) {
                    rewardsSignup = toastURL; continue;
                }
                if (toastURL.contains("/rewards")) {
                    rewards = toastURL; continue;
                }
                if (toastURL.contains("/online-order") || toastURL.contains("locations")) {
                    onlineOrder = toastURL; continue;
                }
                if (toastURL.contains("/giftcards")) {
                    giftcards = toastURL; continue;
                }
                if (toastURL.contains("/findcard")) {
                    findcard = toastURL;
                }
            }
            csvPrinter.printRecord(restaurant.getRestaurantName(), restaurant.getRestaurantURL(), rewards, rewardsSignup, giftcards, findcard, onlineOrder);
        }

        for(String restaurantCSVString : App2_FailedList.listFailedRestaurant(INPUT_FILE_URL, DB_COLLECTION)){
            csvPrinter.printRecord(restaurantCSVString.split(",")[0]
                    , restaurantCSVString.split(",")[2]
                    , "FAILED TO GET"
                    , "FAILED TO GET"
                    , "FAILED TO GET"
                    , "FAILED TO GET"
                    , "FAILED TO GET"
            );
        }



        csvPrinter.flush();
    }

    private static CSVPrinter CSV_Setup() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE));
        CSVFormat csvFormat = CSVFormat.DEFAULT.withEscape('\\');

        CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat
                .withHeader("RestaurantName", "URL", "Rewards", "Rewards Signup", "Gift Cards", "Find Card",
                        "Online Order"));
        csvPrinter.flush();
        return csvPrinter;
    }

    public static void unicifyRestaurant(List<Restaurant> restaurantList){
        Set<Restaurant> set = new LinkedHashSet<>();
        set.addAll(restaurantList);
        restaurantList.clear();
        restaurantList.addAll(set);
    }
}
