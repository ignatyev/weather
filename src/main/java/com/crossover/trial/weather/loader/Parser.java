package com.crossover.trial.weather.loader;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.exceptions.ParseException;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * Created by AnVIgnatev on 10.09.2016.
 */
public class Parser {

    private static final String COLON = ",";
    private static final int PARAMETERS_LENGTH = 11;
    private static final int IATA_POS = 4;
    private static final int LAT_POS = 6;
    private static final int LONG_POS = 7;

    public static AirportData parse(@NotNull String s) throws ParseException {
        String[] split = s.split(COLON);
        if (split.length != PARAMETERS_LENGTH) throw new ParseException("Incorrect number of fields for string: " + s);
        AirportData airportData = new AirportData();
        String iata = split[IATA_POS];
        if(StringUtils.isEmpty(iata)) throw new ParseException("Empty IATA in string: " + s);
        airportData.setIata(iata);
        airportData.setLatitude(Double.parseDouble(split[LAT_POS]));
        airportData.setLongitude(Double.parseDouble(split[LONG_POS]));
        return airportData;
    }
}
