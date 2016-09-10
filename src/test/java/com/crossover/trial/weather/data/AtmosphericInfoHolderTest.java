package com.crossover.trial.weather.data;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.exceptions.AirportAdditionException;
import com.crossover.trial.weather.exceptions.AirportNotFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AtmosphericInfoHolderTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        AtmosphericInfoHolder.clear();
    }

    @Test
    public void addAirport() throws Exception {
        AtmosphericInfoHolder.addAirport("TEST", 0, 0);
        Assert.assertEquals(1, AtmosphericInfoHolder.getAirports().size());
    }

    @Test(expected = AirportAdditionException.class)
    public void addExistingAirport() throws Exception {
        AtmosphericInfoHolder.addAirport("TEST", 0, 0);
        AtmosphericInfoHolder.addAirport("TEST", 0, 0);
    }

    @Test
    public void findAirportData() throws Exception {
        String iataCode = "TEST";
        int latitude = 0, longitude = 0;
        AirportData testAirport = new AirportData();
        testAirport.setIata(iataCode);
        testAirport.setLatitude(latitude);
        testAirport.setLongitude(longitude);

        AtmosphericInfoHolder.addAirport(iataCode, latitude, 0);
        AirportData airportData = AtmosphericInfoHolder.findAirportData(iataCode);
        Assert.assertEquals(testAirport, airportData);

    }

    @Test(expected = AirportNotFoundException.class)
    public void findAbsentAirportData() throws Exception {
        String iataCode = "TEST";
        AtmosphericInfoHolder.findAirportData(iataCode);
    }

    @Test
    public void getAtmosphericInformation() throws Exception {
        Assert.assertEquals(0, AtmosphericInfoHolder.getAtmosphericInformation().size());
        AtmosphericInfoHolder.addAirport("TEST", 0, 0);
    }

    @Test
    public void getAirports() throws Exception {
        Assert.assertEquals(0, AtmosphericInfoHolder.getAirports().size());
        AtmosphericInfoHolder.addAirport("TEST", 0, 0);
        Assert.assertEquals(1, AtmosphericInfoHolder.getAirports().size());
    }

    @Test
    public void removeAirport() throws Exception {
        String iataCode = "TEST";
        AtmosphericInfoHolder.addAirport(iataCode, 0, 0);
        AtmosphericInfoHolder.removeAirport(iataCode);
        Assert.assertEquals(0, AtmosphericInfoHolder.getAirports().size());
        Assert.assertEquals(0, AtmosphericInfoHolder.getAtmosphericInformation().size());
    }

}