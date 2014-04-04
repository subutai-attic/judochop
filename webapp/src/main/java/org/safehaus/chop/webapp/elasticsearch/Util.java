package org.safehaus.chop.webapp.elasticsearch;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Util {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date toDate(String dateStr) {
        Date date = null;

        try {
            date = DATE_FORMAT.parse( dateStr.replaceAll("T", " ") );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static int getInt(Map<String, Object> json, String key) {
        Number n = (Number) json.get(key);
        return n != null ? n.intValue() : 0;
    }

    public static long getLong(Map<String, Object> json, String key) {
        Long n = (Long) json.get(key);
        return n != null ? n : 0;
    }

    public static boolean getBoolean(Map<String, Object> json, String key) {
        Boolean bool = (Boolean) json.get(key);
        return bool != null && bool;
    }

    public static String getString(Map<String, Object> json, String key) {
        return (String) json.get(key);
    }

    /**
     * Converts a string to map. String should have this format: {2=b, 1=a}.
     */
    public static Map<String, String> getMap(Map<String, Object> json, String key) {

        HashMap<String, String> map = new HashMap<String, String>();
        String str = getString(json, key);

        if (StringUtils.isEmpty(str)
                || !str.startsWith("{")
                || !str.endsWith("}")
                || str.equals("{}") ) {
            return map;
        }

        String values[] = StringUtils.substringBetween(str, "{", "}").split(",");

        for (String s : values) {
            map.put(
                StringUtils.substringBefore(s, "="),
                StringUtils.substringAfter(s, "=")
            );
        }

        return map;
    }

}
