package org.pojava.datetime.examples;

import org.pojava.datetime.DateTimeConfig;

public class EuroDateTimeConfig extends DateTimeConfig {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isDmyOrder() {
        return true;
    }
}
