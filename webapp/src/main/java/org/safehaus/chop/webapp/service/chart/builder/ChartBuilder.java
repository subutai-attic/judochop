package org.safehaus.chop.webapp.service.chart.builder;

import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;

public abstract class ChartBuilder {

    public abstract Chart getChart(Params params);

}
