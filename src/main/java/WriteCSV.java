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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class WriteCSV {

    private static final String SAMPLE_CSV_FILE = "./sample.csv";

    static MongoClientURI uri = new MongoClientURI(
            "mongodb+srv://admin:admin@learningcluster-mntmv.mongodb.net/test?retryWrites=true&w=majority");
    static CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    static MongoClient mongoClient = new MongoClient(uri);
    static MongoDatabase database = mongoClient.getDatabase("toast").withCodecRegistry(pojoCodecRegistry);
    static MongoCollection<Restaurant> dbCollection = database.getCollection("restaurantAnalytics2", Restaurant.class);

    public static void main(String[] args) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE));
        CSVFormat csvFormat = CSVFormat.DEFAULT.withEscape('\\');

        CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat
                .withHeader("RestaurantName", "URL", "Rewards", "Rewards Signup", "Gift Cards", "Find Card",
                        "Online Order"));
        csvPrinter.flush();

        ArrayList<Restaurant> restaurantArrayList = dbCollection.find().into(new ArrayList<>());
        for (Restaurant restaurant : restaurantArrayList) {

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

            String finalCSV_Line = String
                    .format("%s,%s,%s,%s,%s,%s,%s", restaurant.getRestaurantName(), restaurant.getRestaurantURL(),
                            rewards, rewardsSignup, giftcards, findcard, onlineOrder);
            csvPrinter.printRecord(finalCSV_Line);
        }
        csvPrinter.flush();
    }
}
