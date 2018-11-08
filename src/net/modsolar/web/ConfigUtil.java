/*
 * @author Ian V. Cuyugan <cuyugan.ian.v@gmail.com>
 * 
 * Loads config property file. 
 * 
 * Allows read/write data on property file.
 * 
 */

package net.modsolar.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.modsolar.constant.Constants;

class ConfigUtil {
    private static ConfigUtil INSTANCE;
    private static Properties propertyFile;

    private ConfigUtil() {
    }
    
    static ConfigUtil getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new ConfigUtil();
        }
        return INSTANCE;
    }
    
    private static void loadConfig() throws FileNotFoundException, IOException {
        propertyFile = new Properties();
        propertyFile.load(new FileInputStream(Constants.CONFIG_FILE.toString()));
    }
	
    String getValue(String key) throws IOException {
        if (null == propertyFile) {
            loadConfig();
        }
        return propertyFile.getProperty(key);
    }
	
    void setValue(String key, String value) throws FileNotFoundException, IOException {
        if (null == propertyFile) {
            loadConfig();
        }
        propertyFile.setProperty(key, value);
        propertyFile.store(new FileOutputStream(Constants.CONFIG_FILE.toString()), null);
    }
	
}
