package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Arrival;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.parsers.exception.ArrivalsDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A parser for the data returned by the Translink arrivals at a stop query
 */
public class ArrivalsParser {

    /*
    private String fileName;

    public ArrivalsParser(String filename) {
        this.fileName = filename;
    }
    */

    public static void parse(Stop stop, String fileName)
            throws IOException, ArrivalsDataMissingException, JSONException {
        DataProvider dataProvider = new FileDataProvider(fileName);

        parseArrivals(stop, dataProvider.dataSourceToString());
    }

    /**
     * Parse arrivals from JSON response produced by TransLink query.  All parsed arrivals are
     * added to the given stop assuming that corresponding JSON object has a RouteNo: and an
     * array of Schedules:
     * Each schedule must have an ExpectedCountdown, ScheduleStatus, and Destination.  If
     * any of the aforementioned elements is missing, the arrival is not added to the stop.
     *
     * @param stop             stop to which parsed arrivals are to be added
     * @param jsonResponse    the JSON response produced by Translink
     * @throws JSONException  when JSON response does not have expected format
     * @throws ArrivalsDataMissingException  when no arrivals are found in the reply
     */

    public static void parseArrivals(Stop stop, String jsonResponse)
            throws JSONException, ArrivalsDataMissingException {

        JSONArray arrivals = new JSONArray(jsonResponse);

        for (int index = 0; index < arrivals.length(); index++) {
            JSONObject anArrival = arrivals.getJSONObject(index);
            parseArrival(anArrival, stop);
        }

        if (stop.getArrivals().isEmpty()) {
            throw new ArrivalsDataMissingException(""); }

    }

    public static void parseArrival(JSONObject anArrival, Stop stop)
            throws JSONException, ArrivalsDataMissingException {

        try {

            String routeNo = anArrival.getString("RouteNo");

            RouteManager rm = RouteManager.getInstance();
            Route r = rm.getRouteWithNumber(routeNo);

            //Route r = new Route(routeNo);

            JSONArray schedules = anArrival.getJSONArray("Schedules");

            for (int index = 0; index < schedules.length(); index++) {
                JSONObject aSchedule = schedules.getJSONObject(index);
                parseSchedule(aSchedule, stop, r);
            }

        }
        catch (JSONException e) {}
        //    throw new ArrivalsDataMissingException("");
        // }

    }

    public static void parseSchedule(JSONObject aSchedule, Stop stop, Route r)
            throws JSONException, ArrivalsDataMissingException {

        /*
        if ((!aSchedule.has("ExpectedCountdown")) || (!aSchedule.has("Destination")) ||
                (!aSchedule.has("ScheduleStatus"))) {
            throw new ArrivalsDataMissingException();
        }
        */
        try {

            Integer expectedCountdown = aSchedule.getInt("ExpectedCountdown");
            String destination = aSchedule.getString("Destination");
            String scheduleStatus = aSchedule.getString("ScheduleStatus");

            Arrival a = new Arrival(expectedCountdown, destination, r);
            a.setStatus(scheduleStatus);
            stop.addArrival(a);
        }
        catch (JSONException e) {
            //throw new ArrivalsDataMissingException("");
        }
    }
}
