package org.apache.usergrid.chop.webapp.service.chart.builder;

import org.apache.usergrid.chop.webapp.service.chart.Chart;
import org.apache.usergrid.chop.webapp.service.chart.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ChartBuilder {

    protected Logger LOG = LoggerFactory.getLogger(getClass());

    public abstract Chart getChart(Params params);

}
