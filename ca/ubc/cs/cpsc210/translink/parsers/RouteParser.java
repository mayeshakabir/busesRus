package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.parsers.exception.RouteDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Parse route information in JSON format.
 */
public class RouteParser {
    private String filename;

    public RouteParser(String filename) {
        this.filename = filename;
    }
    /**
     * Parse route data from the file and add all route to the route manager.
     *
     */
    public void parse() throws IOException, RouteDataMissingException, JSONException{
        DataProvider dataProvider = new FileDataProvider(filename);

        parseRoutes(dataProvider.dataSourceToString());
    }
    /**
     * Parse route information from JSON response produced by Translink.
     * Stores all routes and route patterns found in the RouteManager.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException   when JSON data does not have expected format
     * @throws RouteDataMissingException when
     * <ul>
     *  <li> JSON data is not an array </li>
     *  <li> JSON data is missing Name, StopNo, Routes or location elements for any stop</li>
     * </ul>
     */


    /*
    parse the information about the bus Route and store it in the RouteManager
    - RouteNo
    - Name
    - Patterns (see below) fields

    parse the information about the RoutePatterns and add each of them them to the route
    - PatternNo
    - Destination
    - Direction fields to the routes
    */

    public void parseRoutes(String jsonResponse) throws JSONException, RouteDataMissingException {
        JSONArray routes = new JSONArray(jsonResponse);

        for (int index = 0; index < routes.length(); index++) {
            JSONObject aRoute = routes.getJSONObject(index);
            parseRoute(aRoute);
        }
    }

    public void parseRoute(JSONObject aRoute) throws JSONException, RouteDataMissingException {

        /*
        if ((!aRoute.has("RouteNo")) || (!aRoute.has("Name")) || (!aRoute.has("Patterns"))) {
            throw new RouteDataMissingException();
        }
        */

        try {

            String routeNo = aRoute.getString("RouteNo");
            RouteManager rm = RouteManager.getInstance();

            String name = aRoute.getString("Name");

            Route r = rm.getRouteWithNumber(routeNo, name);
            //Route r = new Route(routeNo);

            JSONArray patterns = aRoute.getJSONArray("Patterns");

            for (int index = 0; index < patterns.length(); index++) {
                JSONObject aPattern = patterns.getJSONObject(index);
                parsePattern(aPattern, r);

            }
        } catch (JSONException e) { throw new RouteDataMissingException(""); }

        //rm.getRouteWithNumber(routeNo, name);

    }

    public void parsePattern(JSONObject aPattern, Route r) throws JSONException, RouteDataMissingException {


        /*

        if ((!aPattern.has("Destination")) || (!aPattern.has("Direction")) || (!aPattern.has("PatternNo"))) {
            throw new RouteDataMissingException();
        }
        */

        try {

            String destination = aPattern.getString("Destination");
            String direction = aPattern.getString("Direction");
            String patternNo = aPattern.getString("PatternNo");

            //RoutePattern rp = new RoutePattern(patternNo, destination, direction, r);
            RoutePattern rp = r.getPattern(patternNo, destination, direction);

        } catch (JSONException e) { throw new RouteDataMissingException("");}
    }
}
