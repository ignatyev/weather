package com.crossover.trial.weather.data;

import com.crossover.trial.weather.AirportData;
import org.junit.Assert;
import org.junit.Test;

public class MathUtilsTest {
    @Test
    public void calculateDistance0() throws Exception {
        AirportData ad1 = new AirportData();
        ad1.setLatitude(0);
        ad1.setLongitude(0);

        AirportData ad2 = new AirportData();
        ad1.setLatitude(0);
        ad1.setLongitude(0);

        Assert.assertEquals(0.0, MathUtils.calculateDistance(ad1, ad2), 0);
    }

    @Test
    public void calculateDistance() throws Exception {
        AirportData ad1 = new AirportData(); //DME
        ad1.setLatitude(55.4103);
        ad1.setLongitude(37.9025);

        AirportData ad2 = new AirportData(); //VKO
        ad2.setLatitude(55.5996);
        ad2.setLongitude(37.2712);

        Assert.assertEquals(40, MathUtils.calculateDistance(ad2, ad1), 10);
    }

}