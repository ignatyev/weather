package com.crossover.trial.weather.rest;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.DataPoint;
import com.crossover.trial.weather.data.AtmosphericInfoHolder;
import com.crossover.trial.weather.exceptions.AirportAdditionException;
import com.crossover.trial.weather.exceptions.AirportNotFoundException;
import com.crossover.trial.weather.exceptions.WeatherException;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {
    private final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

    /**
     * shared gson json to object factory
     */
    private final static Gson gson = new Gson();

    @Override
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @Override
    public Response updateWeather(String iataCode, String pointType, String datapointJson) {
        try {
            AtmosphericInfoHolder.addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
        } catch (WeatherException e) {
            LOGGER.log(Level.SEVERE, "Error while updating weather", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }


    @Override
    public Response getAirports() {
        Set<String> retval = AtmosphericInfoHolder.getAirports().stream()
                .map(AirportData::getIata).collect(Collectors.toSet());
        return Response.status(Response.Status.OK).entity(retval).build();
    }


    @Override
    public Response getAirport(String iata) {
        AirportData ad = null;
        try {
            ad = AtmosphericInfoHolder.findAirportData(iata);
        } catch (AirportNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not find airport " + iata, e);
        }
        return Response.status(Response.Status.OK).entity(ad).build();
    }


    @Override
    public Response addAirport(String iata, String latString, String longString) {
        try {
            if (StringUtils.isEmpty(iata)) throw new AirportAdditionException("iata can not be empty");
            AtmosphericInfoHolder.addAirport(iata.toUpperCase(), Double.valueOf(latString), Double.valueOf(longString));
        } catch (AirportAdditionException e) {
            LOGGER.log(Level.SEVERE, "Error while adding an airport", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (NumberFormatException nfe) {
            LOGGER.log(Level.SEVERE, "Wrong number format", nfe);
            return Response.status(Response.Status.BAD_REQUEST).entity(nfe.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response deleteAirport(String iata) {
        try {
            AtmosphericInfoHolder.removeAirport(iata);
        } catch (AirportNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error while deleting airport " + iata, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }
}
