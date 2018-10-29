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

class FileReader {
	
	/*
	 * Reads CSV file.
	 */
	CkCsv loadCSV(String csvFile, boolean withHeaders) {
		csvFile = Objects.requireNonNull(csvFile, "Missing required CSV File!");
		
		CkCsv ckCsv = new CkCsv();
		ckCsv.put_HasColumnNames(withHeaders);
		if (!ckCsv.LoadFile(csvFile)) {
			throw new NullPointerException("CSV file not found!");
		}
		
		return ckCsv;
	}

}
