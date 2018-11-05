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
			System.out.println("Starting Sequence");
			new Main().initiateRead();
		} catch (IOException | ParseException e) {
			System.err.print(e);
		}
	}
	
	private void initiateRead() throws IOException, ParseException {
		final String apiKey = "rAhpJ0Tjtg2q6YAdYlReaZtl4TntH1Mi4BONTqhj";
		UtilityRateFetcher fetcher = new UtilityRateFetcher(apiKey, OpenEI_Format.JSON, 3);
		
		FileReader reader = new FileReader();
		final String csvFile = "/Users/tensai/Desktop/zip.csv";
		final CkCsv ckCsv = reader.loadCSV(csvFile, true);
		final String API = "https://api.openei.org/utility_rates?version=3&format=json&"
				+ "api_key="+apiKey+"&sector=Residential&approved=true&direction=desc"
				+ "&orderby=startdate&servicetype=bundled&detail=full&limit=10&address=";
		
		ConfigUtil configUtil = new ConfigUtil("app.properties");
		configUtil.load();
		int currentIndex = Integer.parseInt(configUtil.getValue("current_index"));
		final int indexEnd = Integer.parseInt(configUtil.getValue("max_row"));
		
		while (currentIndex <= indexEnd) {
			String zipCode = ckCsv.getCell(currentIndex, 0);
			System.out.println("("+currentIndex + ") Now Processing: "+zipCode);
			String httpsURI = API + zipCode;
			String value = fetcher.getRateByZipCode(httpsURI, configUtil, currentIndex);
			ckCsv.SetCell(currentIndex, 4, value);
			ckCsv.SaveFile(csvFile);
			currentIndex++;
		}
	}
	
}

