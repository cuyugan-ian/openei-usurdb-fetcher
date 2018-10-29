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

import com.chilkatsoft.*;

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
		final String csvFile = "/path/to/file.csv";
		
		// Added this since I'm reading a large CSV file. About 80,000+ rows.
		int indexStart = 0;
		final int indexEnd = 99;
		
		FileReader reader = new FileReader();
		final CkCsv ckCsv = reader.loadCSV(csvFile, true);
		
		while (indexStart <= indexEnd) {
			System.out.println("ZIP CODE: " + ckCsv.getCell(indexStart, 0));
			indexStart++;
		}
		
	}
	
}

