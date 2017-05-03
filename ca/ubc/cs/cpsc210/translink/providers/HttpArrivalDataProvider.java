package ca.ubc.cs.cpsc210.translink.providers;

import ca.ubc.cs.cpsc210.translink.BusesAreUs;
import ca.ubc.cs.cpsc210.translink.model.Arrival;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.Stop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Wrapper for Translink Arrival Data Provider
 */
public class HttpArrivalDataProvider extends AbstractHttpDataProvider {
    private Stop stop;

    public HttpArrivalDataProvider(Stop stop) {
        super();
        this.stop = stop;
    }

    @Override
    /**
     * Produces URL used to query Translink web service for expected arrivals at
     * the stop specified in call to constructor.
     *
     * @returns URL to query Translink web service for arrival data
     *
     * TODO: Complete the implementation of this method (Task 8)
     */
    protected URL getURL() throws MalformedURLException {
        String request;
        int stopNo = stop.getNumber();

        //http://api.translink.ca/rttiapi/v1/stops/60980/estimates?apikey=[APIKey]
        request = "http://api.translink.ca/rttiapi/v1/stops/" + stopNo + "/estimates?apikey=" + BusesAreUs.TRANSLINK_API_KEY;
        System.out.println(request);
        return new URL(request);
    }

    @Override
    public byte[] dataSourceToBytes() throws IOException {
        return new byte[0];
    }
}
