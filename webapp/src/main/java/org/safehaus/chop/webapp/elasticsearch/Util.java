package org.safehaus.chop.webapp.elasticsearch;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Util {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date toDate(String dateStr) throws ParseException {
        return DATE_FORMAT.parse( dateStr.replaceAll("T", " ") );
    }

    public static int getInt(Map<String, Object> json, String key) {
        Number n = (Number) json.get(key);
        return n != null ? n.intValue() : 0;
    }

    public static long getLong(Map<String, Object> json, String key) {
        Long n = (Long) json.get(key);
        return n != null ? n.longValue() : 0;
    }

    public static boolean getBoolean(Map<String, Object> json, String key) {
        Boolean v = (Boolean) json.get(key);
        return v != null ? v.booleanValue() : false;
    }

    public static String getString(Map<String, Object> json, String key) {
        return (String) json.get(key);
    }

}
