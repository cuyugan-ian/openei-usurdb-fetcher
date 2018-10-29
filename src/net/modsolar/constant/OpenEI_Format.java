/*
 * @author Ian V. Cuyugan <cuyugan.ian.v@gmail.com>
 * 
 */

package net.modsolar.constant;

public enum OpenEI_Format {
	JSON("json"),
	JSON_PLAIN("json_plain"),
	CSV("csv");
	
	private OpenEI_Format(String text) {
		this.text = text;
	}
	
	private String text;
	
	public String getText() {
		return this.text;
	}

}

