/*
 * @author Ian V. Cuyugan <cuyugan.ian.v@gmail.com>
 * 
 * Loads config property file. 
 * 
 * Allows read/write data on property file.
 * 
 */

package net.modsolar.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

class ConfigUtil {
    private final String CONFIG_FILE;
    private String APP_CONFIG_PATH;
    private Properties propertyFile;

    ConfigUtil(String configFile) {
        this.CONFIG_FILE = Objects.requireNonNull(configFile, "Identify name of your .properties file.");
    }
	
    void load() throws FileNotFoundException, IOException {
//        String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//        String rootPath = new File(classPath).getParent();
        APP_CONFIG_PATH  = "resources/" + CONFIG_FILE;
        System.out.println("APP_CONFIG_PATH: "+APP_CONFIG_PATH);

        propertyFile = new Properties();
        propertyFile.load(new FileInputStream(APP_CONFIG_PATH));
    }
	
	String getValue(String key) {
		if (null == propertyFile) {
			throw new NullPointerException("Couldn't read on non-existing file.");
		}
		return propertyFile.getProperty(key);
	}
	
	void setValue(String key, String value) throws FileNotFoundException, IOException {
		if (null == propertyFile) {
			throw new NullPointerException("Couldn't write on non-existing file.");
		}
		propertyFile.setProperty(key, value);
		propertyFile.store(new FileOutputStream(APP_CONFIG_PATH), null);
	}
	
}

