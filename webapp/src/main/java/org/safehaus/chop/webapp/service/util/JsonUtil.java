package org.safehaus.chop.webapp.service.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class JsonUtil {

    private static Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

    public static void put(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            LOG.error("Exception while put to json: ", e);
        }
    }

    public static void inc(JSONObject json, String key, long incValue) {
        try {
            long value = json.optLong(key) + incValue;
            json.put(key, value);
        } catch (JSONException e) {
            LOG.error("Exception while put to json: ", e);
        }
    }

    public static void copy(JSONObject src, JSONObject dest) {
        Iterator iter = src.keys();

        try {
            while (iter.hasNext()) {
                String key = (String) iter.next();
                put(dest, key, src.get(key) );
            }
        } catch (JSONException e) {
            LOG.error("Exception while coping json: ", e);
        }
    }

    public static void copy(JSONObject src, JSONObject dest, String key) {
        Object value = src.opt(key);

        if (value != null) {
            put(dest, key, value);
        }
    }

}
