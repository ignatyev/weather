package com.crossover.trial.weather.loader;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.exceptions.ParseException;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by AnVIgnatev on 10.09.2016.
 */
public class Parser {

    private static final String COLON = ",";
    private static final String QUOTE = "\"";
    private static final int PARAMETERS_LENGTH = 11;
    private static final int IATA_POS = 4;
    private static final int LAT_POS = 6;
    private static final int LONG_POS = 7;

    public static AirportData parse(String s) throws ParseException {
        String[] strings = StringUtils.split(s, COLON);
        if (strings == null || strings.length != PARAMETERS_LENGTH)
            throw new ParseException("Incorrect number of fields for string: " + s);
        AirportData airportData = new AirportData();
        String iata = StringUtils.strip(strings[IATA_POS], QUOTE);
        if(StringUtils.isEmpty(iata))
            throw new ParseException("Empty IATA in string: " + s);
        airportData.setIata(iata);
        airportData.setLatitude(Double.parseDouble(strings[LAT_POS]));
        airportData.setLongitude(Double.parseDouble(strings[LONG_POS]));
        return airportData;
    }
}
