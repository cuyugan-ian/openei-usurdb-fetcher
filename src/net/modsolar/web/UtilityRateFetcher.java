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
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class UtilityRateFetcher {
    private static final Logger LOGGER = LogManager.getLogger(UtilityRateFetcher.class);

    private UtilityRateFetcher() {
    }
	
    static String checkRateAvailability(String httpsURI, int currIndex) throws IOException, ParseException {
        httpsURI = Objects.requireNonNull(httpsURI, "Required URL of OpenEI's USURDB!");
        StringBuffer utilities = getUtilities(httpsURI, currIndex);
        if (null == utilities) {
            return "NO";
        }
        JSONParser parser = new JSONParser();
        JSONObject jobj = (JSONObject)parser.parse(utilities.toString());
        JSONArray jsonArr = (JSONArray)jobj.get("errors");
        return (null == jsonArr) ? "YES" : "NO";
    }
	
    private static StringBuffer getUtilities(String httpsURI, int currIndex) throws IOException{
        StringBuffer utilities = null;
        URL url = new URL(httpsURI);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            utilities = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                utilities.append(inputLine);
            }
            inputLine = null;
            in.close();
            in = null;
        } else {
            LOGGER.error("Response Code: "+connection.getResponseCode());
            LOGGER.error("Sequence Terminated");
            ConfigUtil.getInstance().setValue("current_index", String.valueOf(currIndex));
            System.exit(1);
        }
        connection.disconnect();
        connection = null;
        url = null;

        return utilities;
    }
	
}

