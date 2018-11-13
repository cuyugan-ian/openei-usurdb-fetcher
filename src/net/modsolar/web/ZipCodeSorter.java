/*
 * @author Ian V. Cuyugan <cuyugan.ian.v@gmail.com>
 * 
 */
package net.modsolar.web;

import com.chilkatsoft.CkCsv;
import java.util.LinkedHashMap;
import java.util.Map;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;
import net.modsolar.pojo.ZipCount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ZipCodeSorter {
    private static final Logger LOGGER = LogManager.getLogger(ZipCodeSorter.class);
    
    /*
     * Reads all Zip Codes calls by Licensed Active users of modsolar platform. 
     */
    void countFrequentlyUsedZipCodes(final String fileToRead, final String fileToSave, final int maxRow) {
        if (null == fileToRead || fileToRead.trim().isEmpty() || null == fileToSave ||
                fileToSave.trim().isEmpty()) {
            throw new NullPointerException("Check file path!");
        }
        final FileReader reader = new FileReader();
        final CkCsv ckCsv = reader.loadCSV(fileToRead, true);
        
        Map<String, ZipCount> mappedValues = reader.countPerZipCode(ckCsv, 4, maxRow);
        if (null == mappedValues || mappedValues.isEmpty()) {
            LOGGER.error("Empty Map");
            System.exit(1);
        }
        
        saveToFile(mappedValues, fileToSave);
    }
    
    /*
     * Sorts the Map from lowest to highest call counts.
     * Saves Zip Codes and Call count to a CSV file.
     */
    private void sortAndSaveToFile(Map<String, Integer> mappedValues, final String fileToSave) {
        Map<String, Integer> sorted = mappedValues.entrySet()
                .stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), 
                        (e1, e2) -> e2, LinkedHashMap::new));
        
        final FileReader reader = new FileReader();
        final CkCsv ckCsv = reader.loadCSV(fileToSave, true);
        
        int index = 0;
        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            ckCsv.SetCell(index, 0, entry.getKey());
            ckCsv.SetCell(index, 1, String.valueOf(entry.getValue()));
            index++;
        }
        ckCsv.SaveFile(fileToSave);
    }
    
    private void saveToFile(Map<String, ZipCount> mappedValues, final String fileToSave) {
        final FileReader reader = new FileReader();
        final CkCsv ckCsv = reader.loadCSV(fileToSave, true);
        
        int index = 0;
        
        for (Map.Entry<String, ZipCount> entry: mappedValues.entrySet()) {
            ckCsv.SetCell(index, 0, entry.getKey());
            
            ZipCount zipCount = entry.getValue();
            ckCsv.SetCell(index, 1, zipCount.getState());
            ckCsv.SetCell(index, 2, String.valueOf(zipCount.getCount()));
            index++;
        }
        ckCsv.SaveFile(fileToSave);
    }

}
