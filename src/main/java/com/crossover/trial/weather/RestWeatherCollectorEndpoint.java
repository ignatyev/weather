package com.crossover.trial.weather;

import com.crossover.trial.weather.data.AtmosphericInfoHolder;
import com.crossover.trial.weather.exceptions.AirportAdditionException;
import com.google.gson.Gson;

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
    public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

    /** shared gson json to object factory */
    public final static Gson gson = new Gson();

    @Override
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @Override
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String datapointJson) {
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
    public Response getAirport(@PathParam("iata") String iata) {
        AirportData ad = AtmosphericInfoHolder.findAirportData(iata);
        return Response.status(Response.Status.OK).entity(ad).build();
    }


    @Override
    public Response addAirport(@PathParam("iata") String iata,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) {
        try {
            AtmosphericInfoHolder.addAirport(iata, Double.valueOf(latString), Double.valueOf(longString));
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
    public Response deleteAirport(@PathParam("iata") String iata) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Override
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }

}
