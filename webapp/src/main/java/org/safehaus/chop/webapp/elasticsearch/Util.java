package org.safehaus.chop.webapp.elasticsearch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date toDate(String dateStr) throws ParseException {
        return DATE_FORMAT.parse( dateStr.replaceAll("T", " ") );
    }
}
