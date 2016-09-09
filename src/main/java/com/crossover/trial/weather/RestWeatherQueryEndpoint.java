package com.crossover.trial.weather;

import com.crossover.trial.weather.data.AtmosphericInfoHolder;
import com.crossover.trial.weather.data.Statistics;
import com.crossover.trial.weather.exceptions.AirportNotFoundException;
import com.google.gson.Gson;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.crossover.trial.weather.data.AtmosphericInfoHolder.getAtmosphericInformation;
import static com.crossover.trial.weather.data.Statistics.updateRequestFrequency;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

    //TODO use logger
    public final static Logger LOGGER = Logger.getLogger("WeatherQuery");

    /** shared gson json to object factory */
    public static final Gson gson = new Gson();

    static {
        AtmosphericInfoHolder.init();
    }
    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
    @Override
    public String ping() {
        return gson.toJson(Statistics.getStats()); //TODO remove gson?
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iata the iataCode
     * @param radiusString the radius in km
     *
     * @return a list of atmospheric information
     */
    @Override
    public Response weather(String iata, String radiusString) {
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        updateRequestFrequency(iata, radius);

        List<AtmosphericInformation> retval;
        try {
            retval = getAtmosphericInformation(iata, radius);
        } catch (AirportNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error while requesting weather", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }


}
