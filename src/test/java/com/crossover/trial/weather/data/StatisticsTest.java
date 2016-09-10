package com.crossover.trial.weather.data;

import com.crossover.trial.weather.AirportData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static com.crossover.trial.weather.data.Statistics.DATASIZE;
import static com.crossover.trial.weather.data.Statistics.IATA_FREQ;
import static com.crossover.trial.weather.data.Statistics.RADIUS_FREQ;

public class StatisticsTest {

    public static final String IATA_CODE = "TEST";
    public static final String IATA_CODE2 = "TEST2";

    @Before
    public void setUp() throws Exception {
        int latitude = 0, longitude = 0;
        AtmosphericInfoHolder.addAirport(IATA_CODE, latitude, longitude);
    }

    @After
    public void tearDown() throws Exception {
        AtmosphericInfoHolder.clear();
    }

    @Test
    public void updateRequestFrequency() throws Exception {
        AtmosphericInfoHolder.addAirport(IATA_CODE2, 10, 10);


        Statistics.updateRequestFrequency(IATA_CODE, 0.0);
        Map<String, Double> iataFreq = (Map<String, Double>) Statistics.getStats().get(IATA_FREQ);
        Assert.assertEquals(2, iataFreq.size());

        Assert.assertTrue(1 == iataFreq.get(IATA_CODE));
        int[] hist = new int[1];
        hist[0] = 1;
        int[] radiusFreq = (int[]) Statistics.getStats().get(RADIUS_FREQ);
        Assert.assertTrue(Arrays.equals(hist, radiusFreq));

        Statistics.updateRequestFrequency(IATA_CODE, 0.0);

        Assert.assertTrue(1 == iataFreq.get(IATA_CODE));
        hist[0] = 2;
        radiusFreq = (int[]) Statistics.getStats().get(RADIUS_FREQ);
        Assert.assertTrue(Arrays.equals(hist, radiusFreq));

        Statistics.updateRequestFrequency(IATA_CODE2, 10.0);

        iataFreq = (Map<String, Double>) Statistics.getStats().get(IATA_FREQ);
        Assert.assertTrue(2.0/3 == iataFreq.get(IATA_CODE));
        Assert.assertTrue(1.0/3 == iataFreq.get(IATA_CODE2));
        radiusFreq = (int[]) Statistics.getStats().get(RADIUS_FREQ);
        Assert.assertEquals(11, radiusFreq.length);
        Assert.assertEquals(3, radiusFreq[0]);
        Assert.assertEquals(0, radiusFreq[10]);
    }

    @Test
    public void getStats() throws Exception {
        Statistics.updateRequestFrequency(IATA_CODE, 0.0);
        Assert.assertEquals(1, Statistics.getStats().get(DATASIZE));

    }

}