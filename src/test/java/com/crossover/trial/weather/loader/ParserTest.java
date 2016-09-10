package com.crossover.trial.weather.loader;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.exceptions.ParseException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParserTest {
    @Test
    public void parse() throws Exception, ParseException {
        AirportData airportData = Parser.parse("1,\"General Edward Lawrence Logan Intl\",\"Boston\",\"United States\",\"BOS\",\"KBOS\",42.364347,-71.005181,19,-5,\"A\"");

        AirportData airportData1 = new AirportData();
        airportData1.setLatitude(42.364347);
        airportData1.setLongitude(-71.005181);
        airportData1.setIata("BOS");
        Assert.assertEquals(airportData1, airportData);
    }

}