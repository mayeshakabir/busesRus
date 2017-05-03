package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.*;
import ca.ubc.cs.cpsc210.translink.parsers.exception.StopDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A parser for the data returned by Translink stops query
 */
public class StopParser {

    private String filename;

    public StopParser(String filename) {
        this.filename = filename;
    }
    /**
     * Parse stop data from the file and add all stops to stop manager.
     *
     */
    public void parse()
            throws IOException, StopDataMissingException, JSONException {

        DataProvider dataProvider = new FileDataProvider(filename);
        parseStops(dataProvider.dataSourceToString());
    }
    /**
     * Parse stop information from JSON response produced by Translink.
     * Stores all stops and routes found in the StopManager and RouteManager.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException   when JSON data does not have expected format
     * @throws StopDataMissingException when
     * <ul>
     *  <li> JSON data is not an array </li>
     *  <li> JSON data is missing Name, StopNo, Routes or location (Latitude or Longitude) elements for any stop</li>
     * </ul>
     */

    public void parseStops(String jsonResponse) throws JSONException, StopDataMissingException {

        JSONArray stops = new JSONArray(jsonResponse);

        for (int index = 0; index < stops.length(); index++) {
            JSONObject aStop = stops.getJSONObject(index);
            parseStop(aStop);
        }
    }
    public void parseStop(JSONObject aStop) throws JSONException, StopDataMissingException {
        // Name, StopNo, Latitude, Longitude, and Routes fields

        /*
        if ((!aStop.has("Name")) || (!aStop.has("Latitude")) || (!aStop.has("Longitude")) ||
                (!aStop.has("StopNo")) || (!aStop.has("Routes"))) {
            throw new StopDataMissingException();
        }
        */

        try {

            String name = aStop.getString("Name");
            Double latitude = aStop.getDouble("Latitude");
            Double longitude = aStop.getDouble("Longitude");
            Integer stopNo = aStop.getInt("StopNo");

            StopManager sm = StopManager.getInstance();
            LatLon latlon = new LatLon(latitude, longitude);
            Stop s = sm.getStopWithId(stopNo, name, latlon);

            String routeFields = aStop.getString("Routes");
            String[] routes = routeFields.split(",");

            for (int i = 0; i < routes.length; i++) {
                String route = routes[i].trim();
                RouteManager rm = RouteManager.getInstance();
                Route r = rm.getRouteWithNumber(route);
                s.addRoute(r);
                r.addStop(s);
            }

        } catch (JSONException e) { throw new StopDataMissingException(""); }

        //sm.getStopWithId(stopNo, name, latlon);
    }
}
