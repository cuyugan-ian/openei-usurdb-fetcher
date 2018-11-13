/*
 * @author Ian V. Cuyugan <cuyugan.ian.v@gmail.com>
 * 
 * 
 * This class will be used to read and write to files.
 * 
 */

package net.modsolar.web;

import java.util.Objects;
import com.chilkatsoft.*;
import java.util.HashMap;
import java.util.Map;
import net.modsolar.pojo.ZipCount;

class FileReader {
	
    /*
     * Reads CSV file.
     */
    CkCsv loadCSV(String csvFile, boolean withHeaders) {
        csvFile = Objects.requireNonNull(csvFile, "Missing required CSV File!");

        CkCsv ckCsv = new CkCsv();
        ckCsv.put_HasColumnNames(withHeaders);
        //used this to set delimeter of csv file
        //ckCsv.put_Delimiter("\t");
        
        if (!ckCsv.LoadFile(csvFile)) {
            throw new NullPointerException("CSV file not found!");
        }

        return ckCsv;
    }
    
    /*
     * Counts the value "Yes/No" on a column.
     * returns Map<String, Integer>
     */
    Map<String, Integer> countRateAvailability(CkCsv ckCsv, int column, int maxRow) {
        if (null == ckCsv) {
            throw new NullPointerException("CKCSV parameter contains null value!");
        }
        
        final String yes = "YES";
        final String no = "NO";
        int yesCount = 0;
        int noCount = 0;
        
        for (int i=0; i<=maxRow; i++) {
            String cellValue = ckCsv.getCell(i, column);
            if (null == cellValue || cellValue.trim().isEmpty()) {
                System.out.println("NULL (index): "+i);
                continue;
            } else if (cellValue.equalsIgnoreCase(yes)){
                yesCount++;
            } else if (cellValue.equalsIgnoreCase(no)) {
                noCount++;
            }
        }
        
        Map<String, Integer> mappedValues = new HashMap<String, Integer>();
        mappedValues.put(yes, yesCount);
        mappedValues.put(no, noCount);
        return mappedValues;
    }
    
    /*
     * Counts data per Zip Code. 
     * Saves Zip Code as Key and call counts as Value.
     * Returns a Map
     */
    Map<String, ZipCount> countPerZipCode(CkCsv ckCsv, int column, int maxRow) {
        if (null == ckCsv) {
            throw new NullPointerException("CKCSV parameter contains null value!");
        }
        Map<String, ZipCount> mappedValues = new HashMap<String, ZipCount>();
        
        for (int i=0; i<=maxRow; i++) {
            String cellValue = ckCsv.getCell(i, 0);
            System.out.println("Reading ("+i+") Zip Code: "+cellValue);
            if (null == cellValue || cellValue.trim().isEmpty()) {
                continue;
            } else if (mappedValues.containsKey(cellValue)){
                ZipCount zipCount = mappedValues.get(cellValue);
                zipCount.incrementCount();
                mappedValues.put(cellValue, zipCount);
            } else {
                String state = ckCsv.getCell(i, 1);
                ZipCount zipCount = new ZipCount(state, 1);
                mappedValues.put(cellValue, zipCount);
            }
        }
        
        return mappedValues;
    }
    
    
}
