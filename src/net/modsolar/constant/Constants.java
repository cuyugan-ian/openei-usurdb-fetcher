/*
 * @author Ian V. Cuyugan <cuyugan.ian.v@gmail.com>
 * 
 */

package net.modsolar.constant;

public enum Constants {
    API_KEY("YOUR_API_KEY"),
    CSV_FILE("resources/zip.csv"),
    CONFIG_FILE("resources/app.properties");
    
    private Constants(String value) {
        this.value = value;
    }
    
    private String value;
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
}
