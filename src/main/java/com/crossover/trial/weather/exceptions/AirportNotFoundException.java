package com.crossover.trial.weather.exceptions;

/**
 * Created by AnVIgnatev on 09.09.2016.
 */
public class AirportNotFoundException extends WeatherException {

    public AirportNotFoundException(String s) {
        super(String.format("Airport %s not found", s));
    }

}
