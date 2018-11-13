/*
 * @author Ian V. Cuyugan <cuyugan.ian.v@gmail.com>
 * 
 * Reads a CSV file that contains U.S. Zip Codes and uses OpenEI's 
 * U.S. Utility Rate Database API to lookup Utility Rate and updates the CSV file.
 * 
 * Uses Chilkat libraries.
 * 
 * Make sure to set right values on Constants
 * 
 */

package net.modsolar.web;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import com.chilkatsoft.*;
import java.util.Map;
import net.modsolar.constant.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());
	
    static {
        try {
            System.loadLibrary("chilkat");
        } catch (UnsatisfiedLinkError e) {
            LOGGER.error("Native code library failed to load.", e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        try {
            final String fileToRead = "resources/zip_code_requests.csv";
            final String fileToSave = "resources/report1.csv";
            
            ZipCodeSorter sorter = new ZipCodeSorter();
            sorter.countFrequentlyUsedZipCodes(fileToRead, fileToSave, 1364718);
        } catch (RuntimeException e) {
            LOGGER.error("Error!", e);
        }
    }
        
    private void initiateRateChecker() {
        try {
            LOGGER.info("Starting Sequence");
            ConfigUtil configUtil = ConfigUtil.getInstance();
            int currentIndex = Integer.parseInt(configUtil.getValue("current_index"));
            final int maxRow = Integer.parseInt(configUtil.getValue("max_row"));

            if (currentIndex > maxRow) {
                LOGGER.info("Sequence Complete!");
                System.exit(1);
            }

            checkRatePerZipCode(currentIndex, maxRow);
        } catch (ParseException | IOException e) {
            LOGGER.error(e);
        }
    }
    
    private void checkRatePerZipCode(int currentIndex, int maxRow) throws ParseException, IOException {
        FileReader reader = new FileReader();
        final CkCsv ckCsv = reader.loadCSV(Constants.CSV_FILE.toString(), true);

        while (currentIndex <= maxRow) {
            String zipCode = ckCsv.getCell(currentIndex, 0);
            LOGGER.info("("+currentIndex+") ZIP CODE: "+zipCode);
            checkRateAvailability(getURI(zipCode), currentIndex, ckCsv);
            currentIndex++;
        }
    }

    private void checkRateAvailability(String httpsURI, int currentIndex, 
            CkCsv ckCsv) throws ParseException, IOException {
        String value = UtilityRateFetcher.checkRateAvailability(httpsURI, currentIndex);
        ckCsv.SetCell(currentIndex, 2, value);
        ckCsv.SaveFile(Constants.CSV_FILE.toString());
    }
    
    private String getURI(String zipCode) {
        return "https://api.openei.org/utility_rates?version=3&format=json&"
            + "api_key="+Constants.API_KEY+"&sector=Residential&approved=true&direction=desc"
                + "&orderby=startdate&servicetype=bundled&detail=full&limit=10&address="+zipCode;
    }
    
    private void countRateAvailability() {
        final int maxRow = 74021;
        final FileReader reader = new FileReader();
        final CkCsv ckCsv = reader.loadCSV(Constants.CSV_FILE.toString(), true);
        
        Map<String, Integer> mappedValues = reader.countRateAvailability(ckCsv, 4, maxRow);
        if (null == mappedValues || mappedValues.isEmpty()) {
            System.out.println("Empty Map!");
            System.exit(1);
        }
        
        Integer yesCount = (mappedValues.containsKey("YES")) ? mappedValues.get("YES") : null;
        Integer noCount = (mappedValues.containsKey("NO")) ? mappedValues.get("NO") : null;
        
        System.out.println("YES: "+yesCount);
        System.out.println("NO: "+noCount);
    }
    
}