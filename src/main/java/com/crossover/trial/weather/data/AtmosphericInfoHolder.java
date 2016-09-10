package com.crossover.trial.weather.data;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.AtmosphericInformation;
import com.crossover.trial.weather.DataPoint;
import com.crossover.trial.weather.DataPointType;
import com.crossover.trial.weather.exceptions.AirportAdditionException;
import com.crossover.trial.weather.exceptions.AirportNotFoundException;
import com.crossover.trial.weather.exceptions.WeatherException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.crossover.trial.weather.data.MathUtils.calculateDistance;

/**
 * Created by AnVIgnatev on 09.09.2016.
 */
public class AtmosphericInfoHolder {

    private static Map<AirportData, AtmosphericInformation> atmosphericInformation =
            new ConcurrentHashMap<>();

    static {
        init();
    }

    /**
     * Add a new known airport to our list.
     *
     * @param iataCode  3 letter code
     * @param latitude  in degrees
     * @param longitude in degrees
     * @return the added airport
     * @throws AirportAdditionException when an airport with such iataCode, latitude and longitude already exists
     */
    public static AirportData addAirport(String iataCode, double latitude, double longitude)
            throws AirportAdditionException {
        AirportData airport = new AirportData();
        airport.setIata(iataCode);
        airport.setLatitude(latitude);
        airport.setLatitude(longitude);

        AtmosphericInformation ai = new AtmosphericInformation();
        if (atmosphericInformation.putIfAbsent(airport, ai) != null) {
            throw new AirportAdditionException(iataCode + " already exists");
        }
        return airport;
    }

    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode as a string
     * @return airport data
     * @throws AirportNotFoundException if the airport data is not found
     */
    public static AirportData findAirportData(String iataCode) throws AirportNotFoundException {
        return atmosphericInformation.keySet().stream()
                .filter(airport -> airport.getIata().equals(iataCode))
                .findFirst().orElseThrow(() -> new AirportNotFoundException(iataCode));
    }

    public static List<AtmosphericInformation> getAtmosphericInformation(String iata, double radius)
            throws AirportNotFoundException {
        AirportData foundAirport = findAirportData(iata);

        List<AtmosphericInformation> retval;
        if (radius == 0) {
            retval = new ArrayList<>();
            retval.add(atmosphericInformation.get(foundAirport));
        } else {
            retval = atmosphericInformation.entrySet().stream()
                    .filter(entry ->
                            (calculateDistance(foundAirport, entry.getKey()) <= radius) && !entry.getValue().isEmpty())
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }
        return retval;
    }

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode  the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp        a datapoint object holding pointType data
     * @throws WeatherException if the update can not be completed
     */
    public static void addDataPoint(String iataCode, String pointType, DataPoint dp) throws AirportNotFoundException {
        AirportData airportData = findAirportData(iataCode);
        AtmosphericInformation ai = atmosphericInformation.get(airportData);
        updateAtmosphericInformation(ai, pointType, dp);
    }

    /**
     * update atmospheric information with the given data point for the given point type
     *
     * @param ai        the atmospheric information object to update
     * @param pointType the data point type as a string
     * @param dp        the actual data point
     * @throws IllegalArgumentException if the DataPointType does not exist
     */
    private static void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) {
        final DataPointType dptype = DataPointType.valueOf(pointType.toUpperCase());
        switch (dptype) {
            case WIND:
                if (dp.getMean() >= 0) {
                    ai.setWind(dp);
                }
                break;
            case TEMPERATURE:
                if (dp.getMean() >= -50 && dp.getMean() < 100) {
                    ai.setTemperature(dp);
                }
                break;
            case HUMIDTY:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    ai.setHumidity(dp);
                }
                break;
            case PRESSURE:
                if (dp.getMean() >= 650 && dp.getMean() < 800) {
                    ai.setPressure(dp);
                }
                break;
            case CLOUDCOVER:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    ai.setCloudCover(dp);
                }
                break;
            case PRECIPITATION:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    ai.setPrecipitation(dp);
                }
                break;
        }
        ai.setLastUpdateTime(System.currentTimeMillis());
    }

    /**
     * A dummy init method that loads hard coded data
     */
    public static void init() {
        atmosphericInformation.clear();
        Statistics.clear();

        try {
            addAirport("BOS", 42.364347, -71.005181);
            addAirport("EWR", 40.6925, -74.168667);
            addAirport("JFK", 40.639751, -73.778925);
            addAirport("LGA", 40.777245, -73.872608);
            addAirport("MMU", 40.79935, -74.4148747);
        } catch (AirportAdditionException e) {
            e.printStackTrace(); // TODO: 09.09.2016
        }
    }


    public static Collection<AtmosphericInformation> getAtmosphericInformation() {
        return Collections.unmodifiableCollection(atmosphericInformation.values());
    }

    public static Collection<AirportData> getAirports() {
        return Collections.unmodifiableCollection(atmosphericInformation.keySet());
    }

    public static void removeAirport(String iata) throws AirportNotFoundException {
        AirportData airportData = findAirportData(iata);
        atmosphericInformation.remove(airportData);
    }
}
