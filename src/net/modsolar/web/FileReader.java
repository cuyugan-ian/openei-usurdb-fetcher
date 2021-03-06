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
import java.util.LinkedHashMap;
import java.util.Map;
import net.modsolar.pojo.InstallerUsage;
import net.modsolar.pojo.ZipCount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;


class FileReader {
    private static final Logger LOGGER = LogManager.getLogger(FileReader.class);
	
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
    
    /*
     * Groups and counts all proposals generated by an installer 
     * by Zip Code, Master Tariff ID, and Terittory ID
     * 
     */
    void groupInstallerUsage(String fileToRead, int maxRow, String fileToSave) {
        if (null == fileToRead) {
            throw new NullPointerException("fileToRead parameter contains null value!");
        }
        
        final CkCsv ckCsv = loadCSV(fileToRead, true);
        Map<String, InstallerUsage> mappedValues = new HashMap<>();
        
        for (int i = 0; i<=maxRow; i++) {
            System.out.println("Reader Index: "+i);
            String zipCode = ckCsv.getCell(i, 2);
            String tariffId = ckCsv.getCell(i, 3);
            String territtoryId = ckCsv.getCell(i, 4);
            
            if (null == zipCode || zipCode.trim().isEmpty() || null == tariffId ||
                    tariffId.trim().isEmpty() || null == territtoryId || territtoryId.trim().isEmpty()) {
                System.out.println("EMPTY!");
                continue;
            }
            
            InstallerUsage installer;
            String key = zipCode + "-" + tariffId + "-" + territtoryId;
            
            if (mappedValues.containsKey(key)) {
                installer = mappedValues.get(key);
                installer.incrementProposalCount();
            } else {
                String installerName = ckCsv.getCell(i, 0);
                String installerId = ckCsv.getCell(i, 1);

                installer = new InstallerUsage(installerName, installerId, zipCode, 
                        tariffId, territtoryId, 1);
            }
            
            mappedValues.put(key, installer);
        }
        
        saveInstallerUsageToFile(mappedValues, fileToSave);
    }
    
    private void saveInstallerUsageToFile(Map<String, InstallerUsage> mappedValues, 
            String fileToSave) {
        if (null == mappedValues || mappedValues.isEmpty()) {
            LOGGER.error("Empty mappedValues");
            return;
        }
        
        final FileReader reader = new FileReader();
        final CkCsv ckCsv = reader.loadCSV(fileToSave, true);
        
        int index = 0;
        
        for (Map.Entry<String, InstallerUsage> entry: mappedValues.entrySet()) {
            InstallerUsage installer = entry.getValue();
            
            ckCsv.SetCell(index, 0, installer.getInstallerName());
            ckCsv.SetCell(index, 1, installer.getInstallerId());
            ckCsv.SetCell(index, 2, installer.getZipCode());
            ckCsv.SetCell(index, 3, installer.getTariffId());
            ckCsv.SetCell(index, 4, installer.getTerittoryId());
            ckCsv.SetCell(index, 5, String.valueOf(installer.getProposalCount()));
            index++;
        }
        ckCsv.SaveFile(fileToSave);
    }
    
}
