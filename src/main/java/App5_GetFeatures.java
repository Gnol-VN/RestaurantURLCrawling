import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class App5_GetFeatures {
    private static String SAMPLE_CSV_FILE = "shard5-withFeatures.csv";
    public static void main(String[] args) throws Exception {
        String path = "./newShardsWithFeatures/shard5-features.csv";
        Reader reader = Files.newBufferedReader(Paths.get(path));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        CSVPrinter csvPrinter = CSV_Setup();
        HashMap<Restaurant, Integer> newRestaurantMapWithFeatures = new HashMap<>();

        //Create a restaurant map from new FEATURE file
        for (CSVRecord csvRecord : csvParser) {
            // Accessing Values by Column Index
            Restaurant restaurant = new Restaurant();
            restaurant.setRestaurantURL(csvRecord.get(2));
            restaurant.getFeatureSet().add(csvRecord.get(3));
            if(newRestaurantMapWithFeatures.containsKey(restaurant)){
                for(Restaurant restaurantInNewMap : newRestaurantMapWithFeatures.keySet()){
                    if(restaurantInNewMap.getRestaurantURL().trim().equals(restaurant.getRestaurantURL().trim())){
                        restaurantInNewMap.getFeatureSet().add(csvRecord.get(3));
                    }
                }
            }
            else newRestaurantMapWithFeatures.put(restaurant, 0);
        }

        //Read all restaurant from old shard file
        Map<Restaurant, Integer> oldMapFromShard = readRestaurantsFromOldShard();

        for(Entry<Restaurant, Integer> entry : oldMapFromShard.entrySet()){
            String feature_rewardsProgram = "";
            String feature_giftCard = "";
            String feature_onlineOrdering= "";

            if(newRestaurantMapWithFeatures.containsKey(entry.getKey())){
                for(Restaurant restaurant : newRestaurantMapWithFeatures.keySet()){
                    if(restaurant.getRestaurantURL().trim().equals(entry.getKey().getRestaurantURL().trim())){
                        if(restaurant.featureSet.contains("REWARDS_PROGRAM")) feature_rewardsProgram = "REWARDS_PROGRAM";
                        if(restaurant.featureSet.contains("TOAST_GIFT_CARDS")) feature_giftCard = "TOAST_GIFT_CARDS";
                        if(restaurant.featureSet.contains("ONLINE_ORDERING")) feature_onlineOrdering = "ONLINE_ORDERING";
                        break;
                    }
                }
            }

            csvPrinter.printRecord(entry.getKey().getRestaurantName(),
                    entry.getKey().getRestaurantURL(),
                    feature_rewardsProgram,
                    entry.getKey().getRewardsLink(),
                    entry.getKey().getRewardsSignUpLink(),
                    feature_giftCard,
                    entry.getKey().getGiftCardsLink(),
                    entry.getKey().getFindCardsLink(),
                    feature_onlineOrdering,
                    entry.getKey().getOnlineOrderLink()
                    );

        }
        csvPrinter.flush();
        System.out.println("---------------");

    }

    private static Map<Restaurant, Integer> readRestaurantsFromOldShard() throws Exception{
        String path = "./shard2.csv";
        Reader reader = Files.newBufferedReader(Paths.get(path));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        Map<Restaurant, Integer> restaurantMap = new HashMap<>();

        //Create a restaurant map from new FEATURE file
        for (CSVRecord csvRecord : csvParser) {
            // Accessing Values by Column Index
            Restaurant restaurant = new Restaurant();
            restaurant.setRestaurantName(csvRecord.get(0));
            restaurant.setRestaurantURL(csvRecord.get(1));
            restaurant.setRewardsLink(csvRecord.get(2));
            restaurant.setRewardsSignUpLink(csvRecord.get(3));
            restaurant.setGiftCardsLink(csvRecord.get(4));
            restaurant.setFindCardsLink(csvRecord.get(5));
            restaurant.setOnlineOrderLink(csvRecord.get(6));

            restaurantMap.put(restaurant, 0);
        }
        return restaurantMap;
    }

    private static CSVPrinter CSV_Setup() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE));
        CSVFormat csvFormat = CSVFormat.DEFAULT.withEscape('\\');

        CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat
                .withHeader("RestaurantName", "URL",
                        "REWARDS_PROGRAM_FEATURE", "Rewards", "Rewards Signup",
                        "TOAST_GIFT_CARDS_FEATURE", "Gift Cards", "Find Card",
                        "ONLINE_ORDERING_FEATURE", "Online Order"));
        csvPrinter.flush();
        return csvPrinter;
    }
}
