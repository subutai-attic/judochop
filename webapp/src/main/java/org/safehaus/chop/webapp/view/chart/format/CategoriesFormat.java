package org.safehaus.chop.webapp.view.chart.format;

import org.apache.commons.lang.StringUtils;

import java.util.Set;

public class CategoriesFormat {

    public static String format(Set<String> categories) {

        String s = "";

        for (String category : categories) {
            if (!s.isEmpty()) {
                s += ", ";
            }

            s += String.format( "'%s'", StringUtils.abbreviate(category, 10) );
        }

        return String.format("[%s]", s);
    }

}
