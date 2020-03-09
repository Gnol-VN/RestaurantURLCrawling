import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class App3_CheckIfAllRestaurantInCSVFIle_is_inRestaurantFile {
    static String INPUT_FILE_PATH = "./restaurant2.txt";
    static String CSV_PATH = "./shard2.csv";

    public static void main(String[] args) throws IOException {
        List<String> list = Files.readAllLines(new File(INPUT_FILE_PATH).toPath(), Charset.defaultCharset());
        String concatAll = "";
        for(String string: list){
            concatAll = concatAll.concat(string);
        }
        Reader reader = Files.newBufferedReader(Paths.get(CSV_PATH));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        int i = 0;
        for (CSVRecord csvRecord : csvParser) {
            // Accessing Values by Column Index
            String restaurantName = csvRecord.get(0);
            String restaurantURL = csvRecord.get(1);
            if(!concatAll.contains(restaurantName.trim()) || !concatAll.contains(restaurantURL.trim())){
                System.out.println("False - JUST ONE TIME IS OKAY");
                i++;
            }
        }
        System.out.println("True");

    }

}
