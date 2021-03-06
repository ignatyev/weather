package com.crossover.trial.weather.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private static final ConcurrentMap<String, AtomicInteger> airportRequestCounts = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Double, AtomicInteger> radiusRequestCounts = new ConcurrentHashMap<>();

    static final String DATASIZE = "datasize";
    static final String IATA_FREQ = "iata_freq";
    static final String RADIUS_FREQ = "radius_freq";

    /**
     * Records information about how often requests are made
     *
     * @param iata   an iata code
     * @param radius query radius
     */
    public static void updateRequestFrequency(String iata, Double radius) {
        airportRequestCounts.putIfAbsent(iata, new AtomicInteger(0));
        airportRequestCounts.get(iata).incrementAndGet();
        radiusRequestCounts.putIfAbsent(radius, new AtomicInteger(0));
        radiusRequestCounts.get(radius).incrementAndGet();
    }

    public static Map<String, Object> getStats() {
        Map<String, Object> retval = new HashMap<>();

        retval.put(DATASIZE, getDatasize());
        retval.put(IATA_FREQ, getAirportFrequenciesMap());
        retval.put(RADIUS_FREQ, getRadiusesHistogram());

        return retval;
    }

    //TODO clarify what's happening here -- looks like a histogram but incorrect
    private static int[] getRadiusesHistogram() {
        int maxRadius = radiusRequestCounts.keySet().parallelStream()
                .max(Double::compare)
                .orElse(1000.0).intValue() + 1;

        int[] hist = new int[maxRadius];
        for (Map.Entry<Double, AtomicInteger> e : radiusRequestCounts.entrySet()) {
            Double radius = e.getKey();
            if (radius == null || radius < 0) continue;
            int i = radius.intValue() % 10;
            hist[i] += e.getValue().get();
        }
        return hist;
    }

    private static Map<String, Double> getAirportFrequenciesMap() {
        int totalRequests = airportRequestCounts.values().parallelStream().mapToInt(AtomicInteger::get).sum();
        if (totalRequests == 0) return new HashMap<>();
        // fraction of queries
        return getAirports().parallelStream()
                .collect(Collectors.toMap(s -> s, airport ->
                        (double)airportRequestCounts.getOrDefault(airport, new AtomicInteger(0)).get()/totalRequests));
    }

    private static long getDatasize() {
        return AtmosphericInfoHolder.getAtmosphericInformation().parallelStream()
                .filter(ai -> !ai.isEmpty() && ai.getLastUpdateTime() > System.currentTimeMillis() - DAY_IN_MILLIS)
                .count();
    }

    static void clear() {
        airportRequestCounts.clear();
        radiusRequestCounts.clear();
    }
}
