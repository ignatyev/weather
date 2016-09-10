package com.crossover.trial.weather;

/**
 * encapsulates sensor information for a particular location
 */
public class AtmosphericInformation {

    /**
     * temperature in degrees celsius
     */
    private DataPoint temperature;

    /**
     * wind speed in km/h
     */
    private DataPoint wind;

    /**
     * humidity in percent
     */
    private DataPoint humidity;

    /**
     * precipitation in cm
     */
    private DataPoint precipitation;

    /**
     * pressure in mmHg
     */
    private DataPoint pressure;

    /**
     * cloud cover percent from 0 - 100 (integer)
     */
    private DataPoint cloudCover;

    /**
     * the last time this data was updated, in milliseconds since UTC epoch
     */
    private long lastUpdateTime;

    public AtmosphericInformation() {

    }

    public AtmosphericInformation(DataPoint temperature, DataPoint wind, DataPoint humidity, DataPoint percipitation, DataPoint pressure, DataPoint cloudCover) {
        this.temperature = temperature;
        this.wind = wind;
        this.humidity = humidity;
        this.precipitation = percipitation;
        this.pressure = pressure;
        this.cloudCover = cloudCover;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Create atmospheric information with the given data point for the given point type
     *
     * @param pointType the data point type as a string
     * @param dp        the actual data point
     * @throws IllegalArgumentException if the DataPointType does not exist
     */
    public AtmosphericInformation updateAtmosphericInformation(String pointType, DataPoint dp) {
        final DataPointType dptype = DataPointType.valueOf(pointType.toUpperCase());
        this.setLastUpdateTime(System.currentTimeMillis());
        switch (dptype) {
            case WIND:
                if (dp.getMean() >= 0) {
                    this.setWind(dp);
                }
                break;
            case TEMPERATURE:
                if (dp.getMean() >= -50 && dp.getMean() < 100) {
                    this.setTemperature(dp);
                }
                break;
            case HUMIDTY:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    this.setHumidity(dp);
                }
                break;
            case PRESSURE:
                if (dp.getMean() >= 650 && dp.getMean() < 800) {
                    this.setPressure(dp);
                }
                break;
            case CLOUDCOVER:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    this.setCloudCover(dp);
                }
                break;
            case PRECIPITATION:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    this.setPrecipitation(dp);
                }
                break;
        }
        return this;
    }

    public boolean isEmpty() {
        return this.cloudCover == null
                && this.humidity == null
                && this.pressure == null
                && this.precipitation == null
                && this.temperature == null
                && this.wind == null;
    }

    public DataPoint getTemperature() {
        return temperature;
    }

    public void setTemperature(DataPoint temperature) {
        this.temperature = temperature;
    }

    public DataPoint getWind() {
        return wind;
    }

    public void setWind(DataPoint wind) {
        this.wind = wind;
    }

    public DataPoint getHumidity() {
        return humidity;
    }

    public void setHumidity(DataPoint humidity) {
        this.humidity = humidity;
    }

    public DataPoint getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(DataPoint precipitation) {
        this.precipitation = precipitation;
    }

    public DataPoint getPressure() {
        return pressure;
    }

    public void setPressure(DataPoint pressure) {
        this.pressure = pressure;
    }

    public DataPoint getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(DataPoint cloudCover) {
        this.cloudCover = cloudCover;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
