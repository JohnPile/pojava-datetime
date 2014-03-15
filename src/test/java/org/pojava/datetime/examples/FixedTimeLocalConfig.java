package org.pojava.datetime.examples;

import org.pojava.datetime.IDateTimeConfig;
import org.pojava.datetime.LocalConfig;

import java.util.TimeZone;


public class FixedTimeLocalConfig extends LocalConfig {

    private long fixedSystemTime;

    protected FixedTimeLocalConfig(IDateTimeConfig config, TimeZone inputTimeZone, TimeZone outputTimeZone, long fixedSystemTime) {
        super(config, inputTimeZone, outputTimeZone);
        this.fixedSystemTime = fixedSystemTime;
    }

    public static FixedTimeLocalConfig instanceOverridingTimeZones(IDateTimeConfig baseConfig, TimeZone inputTimeZone, TimeZone outputTimeZone, long fixedSystemTime) {
        return new FixedTimeLocalConfig(baseConfig, inputTimeZone, outputTimeZone, fixedSystemTime);
    }

    @Override
    public long systemTime() {
        return fixedSystemTime;
    }
}
