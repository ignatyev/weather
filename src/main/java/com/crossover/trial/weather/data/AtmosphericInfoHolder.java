package com.crossover.trial.weather.data;

import com.crossover.trial.weather.*;
import com.crossover.trial.weather.exceptions.AirportAdditionException;
import com.crossover.trial.weather.exceptions.AirportNotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.crossover.trial.weather.data.MathUtils.calculateDistance;

/**
 * Created by AnVIgnatev on 09.09.2016.
 */
public class AtmosphericInfoHolder {
    //TODO encapsulate
    /**
     * all known airports
     */
//    public static List<AirportData> airportData = new ArrayList<>();
    /**
     * atmospheric information for each airport, idx corresponds with airportData
     */
//    static List<AtmosphericInformation> atmosphericInformation = new LinkedList<>();

    private static Map<AirportData, AtmosphericInformation> atmosphericInformation =
            new ConcurrentHashMap<>();

    /**
     * Add a new known airport to our list.
     *
     * @param iataCode  3 letter code
     * @param latitude  in degrees
     * @param longitude in degrees
     * @return the added airport
     * @throws AirportAdditionException when an airport with such an iataCode already exists
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
     * @return airport data or null if not found
     */
    public static AirportData findAirportData(String iataCode) {
        return atmosphericInformation.keySet().stream()
                .filter(airport -> airport.getIata().equals(iataCode))
                .findFirst().orElse(null);
    }

    public static List<AtmosphericInformation> getAtmosphericInformation(String iata, double radius) throws AirportNotFoundException {
        List<AtmosphericInformation> retval = new ArrayList<>();
        AirportData foundAirport = findAirportData(iata);
        if (foundAirport == null) throw new AirportNotFoundException("Airport not found: " + iata);

        if (radius == 0) {
            retval.add(atmosphericInformation.get(foundAirport));
        } else {
            atmosphericInformation.entrySet().stream()
                    .filter(entry -> calculateDistance(foundAirport, entry.getKey()) <= radius)
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
    public static void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
        AirportData airportData = findAirportData(iataCode);
        if (airportData == null) {
            throw new WeatherException(String.format("Airport %s was not found", iataCode));
        }
        AtmosphericInformation ai = atmosphericInformation.get(airportData);
        updateAtmosphericInformation(ai, pointType, dp);
    }

    /**
     * update atmospheric information with the given data point for the given point type
     *
     * @param ai        the atmospheric information object to update
     * @param pointType the data point type as a string
     * @param dp        the actual data point
     */
    private static void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp)
            throws WeatherException {
        final DataPointType dptype = DataPointType.valueOf(pointType.toUpperCase());

        if (pointType.equalsIgnoreCase(DataPointType.WIND.name())) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.TEMPERATURE.name())) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.HUMIDTY.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.PRESSURE.name())) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.CLOUDCOVER.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.PRECIPITATION.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        throw new WeatherException("couldn't update atmospheric data");
    }

    /**
     * A dummy init method that loads hard coded data
     */
    public static void init() {
//        airportData.clear();
        atmosphericInformation.clear();
        Statistics.airportRequestCounts.clear();

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
}
