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

public class Main {
	
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
			final String csvFile = "/path/to/csv.file";
			final String destFile = "/path/to/csv.file";
			
			// Added this since I'm reading a large CSV file. About 80,000+ rows.
			final int indexStart = 0;
			final int indexEnd = 74021;
			
			long startTime = System.currentTimeMillis();
//			new Main().initiateRead(csvFile, indexStart, indexEnd, destFile);
			
			
			final String apiKey = "YOUR_API_KEY";
			UtilityRateFetcher fetcher = new UtilityRateFetcher(apiKey, OpenEI_Format.JSON, 3);
			
			String httpsURI = "https://api.openei.org/utility_rates?version=3&format=json&api_key=YOUR_API_KEY&sector=Residential&approved=true&address=50701&direction=desc&orderby=startdate&servicetype=bundled&detail=full&limit=10";
			fetcher.getRateByZipCode(httpsURI);
			
			long endTime = System.currentTimeMillis();
			System.out.println("Processing Time: " + ((endTime-startTime) * 0.001));
		} catch (IOException | ParseException e) {
			System.err.print(e);
		}
	}
	
	private void initiateRead(String csvFile, int indexStart, int indexEnd, String destFile) {
		FileReader reader = new FileReader();
		final CkCsv ckCsv = reader.loadCSV(csvFile, true);
		
		while (indexStart <= indexEnd) {
//			String zipCode = ckCsv.getCell(indexStart, 0);
			indexStart++;
		}
		ckCsv.SaveFile(destFile);
	}
	
}

