/*
 * @author Ian V. Cuyugan <cuyugan.ian.v@gmail.com>
 * 
 * Retrieves the Utility Rate using OpenEI's USURDB API.
 * 
 */

package net.modsolar.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.modsolar.constant.OpenEI_Format;

class UtilityRateFetcher {
	final String API_KEY;
	final OpenEI_Format FORMAT;
	final int VERSION;
	
	UtilityRateFetcher(String apiKey, OpenEI_Format format, int version) {
		this.API_KEY = apiKey;
		this.FORMAT = format;
		this.VERSION = version;
	}
	
	/*
	 * For list of parameters for OpenEI's USURDB check link below
	 * (https://openei.org/services/doc/rest/util_rates/?version=3)
	 */
	Double getRateByZipCode(Map<String, String> params) throws NullPointerException {
		params = Objects.requireNonNull(params, "Parameters are required!");
		if (params.isEmpty()) {
			throw new NullPointerException("Required atleast one parameter!");
		}
		if (!params.containsKey("address")) {
			throw new NullPointerException("Address parameter is required!");
		}
		
		
		
		return null;
	}
	
	String getRateByZipCode(String httpsURI) throws IOException, ParseException {
		httpsURI = Objects.requireNonNull(httpsURI, "Required URL of OpenEI's USURDB!");
		StringBuffer utilities = this.getUtilities(httpsURI);
		if (null == utilities) {
			return "NO";
		}
		JSONParser parser = new JSONParser();
		JSONObject jobj = (JSONObject)parser.parse(utilities.toString());
		JSONArray jsonArr = (JSONArray)jobj.get("errors");
		return (null == jsonArr) ? "YES" : "NO";
	}
	
	private StringBuffer getUtilities(String httpsURI) throws IOException{
		StringBuffer utilities = null;
		URL url = new URL(httpsURI);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		if (con.getResponseCode() == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			utilities = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    utilities.append(inputLine);
			}
			inputLine = null;
			in.close();
			in = null;
		} else {
			System.err.println("Response Code: "+con.getResponseCode());
			System.out.println("Terminating Sequence");
			System.exit(1);
		}
		con.disconnect();
		con = null;
		url = null;
		
		return utilities;
	}
	
	/*
	 * in progress 
	 */
	private Double getRate(StringBuffer utilities) throws ParseException {
		if (null == utilities) {
			return null;
		}
		JSONParser parser = new JSONParser();
		JSONObject jobj = (JSONObject)parser.parse(utilities.toString());
		JSONArray jsonArr = (JSONArray)jobj.get("items");
		
		System.out.println("Size of Array: " + jsonArr.size());
		int arrSize = jsonArr.size();
		for (int i=0; i<arrSize; i++) {
			JSONObject jsonobj_1 = (JSONObject)jsonArr.get(i);
			System.out.println("label: "+jsonobj_1.get("label"));
			System.out.println("fixedMonthly: "+jsonobj_1.get("fixedmonthlycharge"));
			System.out.println("---");
		}
		
		return null;
	}
	
//	private String getURL(Map<String, String> params) {
//		String url = "https://api.openei.org/utility_rates?";
//		
//		for (Map.Entry<String, String> entry: params.entrySet()) {
//			if (null == entry.getValue()) {
//				continue;
//			}
//			
//		}
//		
//		return null;
//	}
//	

}

