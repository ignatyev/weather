package com.crossover.trial.weather.data;

import com.crossover.trial.weather.AirportData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.crossover.trial.weather.data.AtmosphericInfoHolder.findAirportData;
import static com.crossover.trial.weather.data.AtmosphericInfoHolder.getAirports;

/**
 * Created by AnVIgnatev on 09.09.2016.
 */
public class Statistics {
    /**
     * 1 full day in milliseconds
     */
    private static final int DAY_IN_MILLIS = 86_400_000;
    /**
     * Internal performance counter to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics
     */
    private static Map<AirportData, AtomicInteger> airportRequestCounts = new ConcurrentHashMap<>();
    /**
     * TODO add doc
     */
    private static Map<Double, AtomicInteger> radiusRequestCounts = new ConcurrentHashMap<>();

    /**
     * Records information about how often requests are made
     *
     * @param iata   an iata code
     * @param radius query radius
     */
    public static void updateRequestFrequency(String iata, Double radius) {
        AirportData airportData = findAirportData(iata);
        airportRequestCounts.putIfAbsent(airportData, new AtomicInteger(0));
        airportRequestCounts.get(airportData).incrementAndGet();
        radiusRequestCounts.putIfAbsent(radius, new AtomicInteger(0));
        radiusRequestCounts.get(radius).incrementAndGet();
    }

    public static Map<String, Object> getStats() {
        // TODO: 09.09.2016 synchronize on both collections

        Map<String, Object> retval = new HashMap<>();

        retval.put("datasize", getDatasize());
        retval.put("iata_freq", getAirportFrequenciesMap());
        retval.put("radius_freq", getRadiusesHistogram());

        return retval;
    }

    private static int[] getRadiusesHistogram() {
        int maxRadius = radiusRequestCounts.keySet().stream()
                .max(Double::compare)
                .orElse(1000.0).intValue() + 1;

        int[] hist = new int[maxRadius];
        for (Map.Entry<Double, AtomicInteger> e : radiusRequestCounts.entrySet()) {
            Double radius = e.getKey();
            if (radius == null || radius < 0) continue;
            // FIXME: 09.09.2016 WTF??
            int i = radius.intValue() % 10;
            hist[i] += e.getValue().get();
        }
        return hist;
    }

    private static Map<String, Double> getAirportFrequenciesMap() {
        Map<String, Double> freq = new HashMap<>();
        double totalRequests = airportRequestCounts.values().stream().mapToInt(AtomicInteger::get).sum();
        if (totalRequests == 0) return freq;
//TODO        airportRequestCounts.entrySet().stream().collect(Collectors.)
        // fraction of queries
        for (AirportData airportData : getAirports()) {
            AtomicInteger airportRequests = airportRequestCounts.get(airportData);
            double airportReqCount;
            if (airportRequests == null) {
                airportReqCount = 0;
            } else {
                airportReqCount = airportRequests.get();
            }
            double frac = airportReqCount / totalRequests;
            freq.put(airportData.getIata(), frac);
        }
        return freq;
    }

    private static long getDatasize() {
        return AtmosphericInfoHolder.getAtmosphericInformation().stream()
                .filter((ai) -> !ai.isEmpty() && ai.getLastUpdateTime() > System.currentTimeMillis() - DAY_IN_MILLIS)
                // TODO: 09.09.2016 refactor date
                .count();
    }

    //TODO remove
    static void clear() {
        airportRequestCounts.clear();
        radiusRequestCounts.clear();
    }
}
