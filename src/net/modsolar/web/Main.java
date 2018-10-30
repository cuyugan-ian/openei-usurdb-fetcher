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
			final String csvFile = "/path/to/file.csv";
			final String destFile = "/path/to/file.csv";
			
			/* Added this since I'm reading a large CSV file. About 70,000+ rows.
			 * 
			 * OpenEI's USURDB API only allows 1,000 requests per hour. If the 
			 * allowable 1,000 requests has been reached, the API will return a
			 * 429 Response Code w/ message "OVER_RATE_LIMIT"
			 */
			final int indexStart = 2977;
			final int indexEnd = 74022;
			System.out.println("Starting Sequence");
			new Main().initiateRead(csvFile, indexStart, indexEnd, destFile);
		} catch (IOException | ParseException e) {
			System.err.print(e);
		}
	}
	
	private void initiateRead(String csvFile, int indexStart, int indexEnd, String destFile) throws IOException, ParseException {
		final String apiKey = "YOUR_API_KEY";
		UtilityRateFetcher fetcher = new UtilityRateFetcher(apiKey, OpenEI_Format.JSON, 3);
		
		FileReader reader = new FileReader();
		final CkCsv ckCsv = reader.loadCSV(csvFile, true);
		
		final String API = "https://api.openei.org/utility_rates?version=3&format=json&"
				+ "api_key="+apiKey+"&sector=Residential&approved=true&direction=desc"
				+ "&orderby=startdate&servicetype=bundled&detail=full&limit=10&address=";
		
		while (indexStart <= indexEnd) {
			String zipCode = ckCsv.getCell(indexStart, 0);
			System.out.println("("+indexStart + ") Now Processing: "+zipCode);
			String httpsURI = API + zipCode;
			String value = fetcher.getRateByZipCode(httpsURI);
			ckCsv.SetCell(indexStart, 4, value);
			ckCsv.SaveFile(destFile);
			indexStart++;
		}
	}
	
}

