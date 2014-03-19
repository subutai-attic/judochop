package org.safehaus.chop.webapp.view.chart;

import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.view.chart.view.ChartView;

public interface ChartViewContext {

    public void show(ChartView chartView, Params params);

}
