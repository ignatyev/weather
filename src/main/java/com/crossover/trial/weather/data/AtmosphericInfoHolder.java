package com.crossover.trial.weather.data;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.AtmosphericInformation;
import com.crossover.trial.weather.DataPoint;
import com.crossover.trial.weather.DataPointType;
import com.crossover.trial.weather.exceptions.AirportAdditionException;
import com.crossover.trial.weather.exceptions.AirportNotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.crossover.trial.weather.data.MathUtils.calculateDistance;

/**
 * Created by AnVIgnatev on 09.09.2016.
 */
public class AtmosphericInfoHolder {

    private static ConcurrentMap<AirportData, AtmosphericInformation> atmosphericInformation =
            new ConcurrentHashMap<>();

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
        return atmosphericInformation.keySet().parallelStream()
                .filter(airport -> airport.getIata().equals(iataCode))
                .findFirst().orElseThrow(() -> new AirportNotFoundException(iataCode));
    }

    public static List<AtmosphericInformation> getAtmosphericInformation(String iata, double radius)
            throws AirportNotFoundException {
        AirportData foundAirport = findAirportData(iata);

        List<AtmosphericInformation> retval;
        if (radius == 0) {
            retval = Collections.singletonList(atmosphericInformation.get(foundAirport));
        } else {
            retval = Collections.unmodifiableList(
                    atmosphericInformation.entrySet().parallelStream()
                            .filter(entry ->
                                    (Double.compare(calculateDistance(foundAirport, entry.getKey()), radius) <= 0)
                                            && !entry.getValue().isEmpty())
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList()));
        }
        return retval;
    }

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode  the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp        a datapoint object holding pointType data
     * @throws AirportNotFoundException if the airport with such iata code can not be found
     */
    public static void addDataPoint(String iataCode, String pointType, DataPoint dp)
            throws AirportNotFoundException {
        AirportData airportData = findAirportData(iataCode);
        atmosphericInformation.compute(airportData,
                (airport, ai) -> ai.updateAtmosphericInformation(pointType, dp));
    }

    public static Collection<AtmosphericInformation> getAtmosphericInformation() {
        return Collections.unmodifiableCollection(atmosphericInformation.values());
    }

    public static Set<String> getAirports() {
        return atmosphericInformation.keySet().parallelStream()
                .map(AirportData::getIata).collect(Collectors.toSet());
    }

    public static void removeAirport(String iata) throws AirportNotFoundException {
        AirportData airportData = findAirportData(iata);
        atmosphericInformation.remove(airportData);
    }

    public static void clear() {
        atmosphericInformation.clear();
        Statistics.clear();
    }
}
