package org.safehaus.chop.webapp.service.chart.value;

import org.json.simple.JSONObject;

public class Value {

    protected double value;
    protected long failures;
    protected long ignores;

    protected JSONObject properties = new JSONObject();

    public double getValue() {
        return value;
    }

    public long getFailures() {
        return failures;
    }

    public JSONObject getProperties() {
        return properties;
    }
}
