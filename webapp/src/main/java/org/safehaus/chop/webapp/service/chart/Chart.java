package org.safehaus.chop.webapp.service.chart;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Chart {

    private List<Series> series;
    private Set<String> categories = new HashSet<String>();

    public Chart(List<Series> series, Set<String> categories) {
        this.categories = categories;
        this.series = series;
    }

    public List<Series> getSeries() {
        return series;
    }

    public Set<String> getCategories() {
        return categories;
    }
}
