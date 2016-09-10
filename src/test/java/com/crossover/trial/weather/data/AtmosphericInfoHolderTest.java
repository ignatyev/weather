package com.crossover.trial.weather.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AtmosphericInfoHolderTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void addAirport() throws Exception {
        AtmosphericInfoHolder.addAirport("TEST", 0, 0);
        Assert.assertEquals(1, AtmosphericInfoHolder.getAirports().size());
    }

    @Test
    public void findAirportData() throws Exception {

    }

    @Test
    public void getAtmosphericInformation() throws Exception {

    }

    @Test
    public void addDataPoint() throws Exception {

    }

    @Test
    public void getAtmosphericInformation1() throws Exception {

    }

    @Test
    public void getAirports() throws Exception {

    }

    @Test
    public void removeAirport() throws Exception {

    }

}