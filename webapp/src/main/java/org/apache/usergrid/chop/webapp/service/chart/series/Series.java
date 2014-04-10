package org.apache.usergrid.chop.webapp.service.chart.series;

import org.apache.usergrid.chop.webapp.service.chart.Point;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

public class Series {

    private String name;
    private List<Point> points;

    public Series(String name, List<Point> points) {
        this.name = name;
        this.points = points;
    }

    public Series(List<Point> points) {
        this("", points);
    }

    public List<Point> getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("points", points)
                .toString();
    }
}
