package com.crossover.trial.weather;

import com.crossover.trial.weather.exceptions.ParseException;
import com.crossover.trial.weather.loader.Parser;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static javax.ws.rs.HttpMethod.POST;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 * <p>
 *
 * @author code test administrator
 */
public class AirportLoader {

    private static final String BASE_URI = "http://localhost:9090/";
    private static final String AIRPORT_URI = "airport";
    /**
     * end point for read queries
     */
    private WebTarget query;

    /**
     * end point to supply updates
     */
    private WebTarget collect;

    public AirportLoader() {
        Client client = ClientBuilder.newClient();
        query = client.target(BASE_URI + "query");
        collect = client.target(BASE_URI + "collect");
    }

    public static void main(String args[]) throws IOException {
        File airportDataFile = new File(args[0]);
        if (!airportDataFile.exists() || airportDataFile.length() == 0) {
            System.err.println(airportDataFile + " is not a valid input");
            System.exit(1);
        }

        AirportLoader al = new AirportLoader();
        al.upload(new FileInputStream(airportDataFile));
        System.exit(0);
    }

    public void upload(InputStream airportDataStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream))) {
            String l;
            while ((l = reader.readLine()) != null) {
                try {
                    AirportData airportData = Parser.parse(l);
                    collect.path(AIRPORT_URI)
                            .path(airportData.getIata())
                            .path(Double.toString(airportData.getLatitude()))
                            .path(Double.toString(airportData.getLongitude()))
                            .request().method(POST);
                } catch (ParseException | NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
