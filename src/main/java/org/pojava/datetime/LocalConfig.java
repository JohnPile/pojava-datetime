package org.pojava.datetime;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * LocalConfig is a wrapper class providing a cheap equivalent to the global instance of
 * DateTimeConfig with one or more methods (e.g. TimeZone) overridden.
 */
public class LocalConfig implements IDateTimeConfig {
    private TimeZone inputTimeZone;
    private TimeZone outputTimeZone;
    private IDateTimeConfig config;

    public static LocalConfig instanceOverridingOutputTimeZone(IDateTimeConfig baseConfig, TimeZone outputTimeZone) {
        return new LocalConfig(baseConfig, baseConfig.getInputTimeZone(), outputTimeZone);
    }

    public static LocalConfig instanceOverridingTimeZones(IDateTimeConfig baseConfig, TimeZone inputTimeZone, TimeZone outputTimeZone) {
        return new LocalConfig(baseConfig, inputTimeZone, outputTimeZone);
    }

    protected LocalConfig(IDateTimeConfig config, TimeZone inputTimeZone, TimeZone outputTimeZone) {
        this.config = config;
        this.inputTimeZone = inputTimeZone;
        this.outputTimeZone = outputTimeZone;
    }

    @Override
    public boolean isDmyOrder() {
        return config.isDmyOrder();
    }

    @Override
    public Map<String, String> getTzMap() {
        return config.getTzMap();
    }

    @Override
    public TimeZone getInputTimeZone() {
        return inputTimeZone;
    }

    @Override
    public TimeZone getOutputTimeZone() {
        return outputTimeZone;
    }

    @Override
    public Locale getLocale() {
        return config.getLocale();
    }

    @Override
    public String getFormat() {
        return config.getFormat();
    }

    @Override
    public String getBcPrefix() {
        return config.getBcPrefix();
    }

    @Override
    public int getEpochDOW() {
        return config.getEpochDOW();
    }

    @Override
    public TimeZone lookupTimeZone(String str) {
        return config.lookupTimeZone(str);
    }

    @Override
    public Integer lookupMonthIndex(String monthNameOrAbbreviation) {
        return config.lookupMonthIndex(monthNameOrAbbreviation);
    }

    @Override
    public boolean isUnspecifiedCenturyAlwaysInPast() {
        return config.isUnspecifiedCenturyAlwaysInPast();
    }

    @Override
    public long systemTime() {
        return config.systemTime();
    }

    @Override
    public void validate() {
        config.validate();
    }
}
