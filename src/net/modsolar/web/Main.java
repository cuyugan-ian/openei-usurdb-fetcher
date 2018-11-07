/*
 * @author Ian V. Cuyugan <cuyugan.ian.v@gmail.com>
 * 
 * Reads a CSV file that contains U.S. Zip Codes and uses OpenEI's 
 * U.S. Utility Rate Database API to lookup Utility Rate and updates the CSV file.
 * 
 * Uses Chilkat libraries.
 * 
 */

package net.modsolar.web;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import com.chilkatsoft.*;
import net.modsolar.constant.OpenEI_Format;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());
    private final String CSV_FILE = "resources/zip.csv";
    private final String API_KEY = "rAhpJ0Tjtg2q6YAdYlReaZtl4TntH1Mi4BONTqhj";
    private final UtilityRateFetcher RATE_FETCHER = new UtilityRateFetcher(API_KEY, OpenEI_Format.JSON, 3);
	
    static {
        try {
            System.loadLibrary("chilkat");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        try {
            LOGGER.info("Starting Sequence");
            new Main().initiateRateChecker();
        } catch (ParseException | IOException e) {
            LOGGER.error(e);
        }
    }
        
    private void initiateRateChecker() throws IOException, ParseException {
        ConfigUtil configUtil = new ConfigUtil("app.properties");
        configUtil.load();
        int currentIndex = Integer.parseInt(configUtil.getValue("current_index"));
        final int maxRow = Integer.parseInt(configUtil.getValue("max_row"));

        if (currentIndex > maxRow) {
            LOGGER.info("Sequence Complete!");
            System.exit(1);
        }

        FileReader reader = new FileReader();
        final CkCsv ckCsv = reader.loadCSV(CSV_FILE, true);
        final String API = "https://api.openei.org/utility_rates?version=3&format=json&"
                        + "api_key="+API_KEY+"&sector=Residential&approved=true&direction=desc"
                        + "&orderby=startdate&servicetype=bundled&detail=full&limit=10&address=";

        while (currentIndex <= maxRow) {
            String zipCode = ckCsv.getCell(currentIndex, 0);
            LOGGER.info("("+currentIndex + ") ZIP CODE: "+zipCode);
            String httpsURI = API + zipCode;
            checkRateAvailability(httpsURI, configUtil, currentIndex, ckCsv);
            currentIndex++;
        }
    }

    private void checkRateAvailability(String httpsURI, ConfigUtil configUtil, 
            int currentIndex, CkCsv ckCsv) throws ParseException, IOException {
        String value = RATE_FETCHER.checkRateAvailability(httpsURI, configUtil, currentIndex);
        ckCsv.SetCell(currentIndex, 4, value);
        ckCsv.SaveFile(CSV_FILE);
    }
	
}