package org.safehaus.chop.webapp.view.chart.layout.item;

import com.vaadin.ui.Table;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.util.JsonUtil;

import java.text.DecimalFormat;

public class DetailsTable extends Table {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#");

    public DetailsTable() {
        setWidth("250px");
        setHeight("250px");
        addContainerProperty("Details", String.class,  null);
        addContainerProperty("Value",  String.class,  null);
    }

    public void setContent(JSONObject json) {
        removeAllItems();
        addValues(json);
    }

    private void addValues(JSONObject json) {
        for ( String key : JsonUtil.getKeys(json) ) {
            if ( !"id".equals(key) ) {
                addItem(new Object[]{ key, getValue(json, key) }, key);
            }
        }
    }

    private String getValue(JSONObject json, String key) {

        Object value = json.opt(key);

        return value instanceof Double
            ? DECIMAL_FORMAT.format(value)
            : json.optString(key);
    }

}