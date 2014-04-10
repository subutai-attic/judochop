package org.apache.usergrid.chop.webapp.service.chart.builder;

import com.google.inject.Inject;
<<<<<<< HEAD:webapp/src/main/java/org/safehaus/chop/webapp/service/chart/builder/RunsChartBuilder.java
import org.apache.usergrid.chop.api.Run;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.series.Series;
import org.safehaus.chop.webapp.service.chart.filter.FailureFilter;
import org.safehaus.chop.webapp.service.chart.filter.PercentileFilter;
import org.safehaus.chop.webapp.service.chart.group.GroupByRunNumber;
import org.safehaus.chop.webapp.service.chart.series.SeriesBuilder;
import org.safehaus.chop.webapp.service.chart.value.Value;
=======
import org.apache.usergrid.chop.webapp.service.chart.series.SeriesBuilder;
import org.apache.usergrid.chop.webapp.service.chart.value.Value;
import org.safehaus.chop.api.Run;
import org.apache.usergrid.chop.webapp.dao.RunDao;
import org.apache.usergrid.chop.webapp.service.chart.Chart;
import org.apache.usergrid.chop.webapp.service.chart.Params;
import org.apache.usergrid.chop.webapp.service.chart.series.Series;
import org.apache.usergrid.chop.webapp.service.chart.filter.FailureFilter;
import org.apache.usergrid.chop.webapp.service.chart.filter.PercentileFilter;
import org.apache.usergrid.chop.webapp.service.chart.group.GroupByRunNumber;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:webapp/src/main/java/org/apache/usergrid/chop/webapp/service/chart/builder/RunsChartBuilder.java

import java.util.*;

public class RunsChartBuilder extends ChartBuilder {

    private RunDao runDao;

    @Inject
    public RunsChartBuilder(RunDao runDao) {
        this.runDao = runDao;
    }

    public Chart getChart(Params params) {

        List<Run> runs = runDao.getList( params.getCommitId(), params.getTestName() );

        Collection<Value> groupedRuns = new GroupByRunNumber(runs, params.getMetric() ).get();

        Collection<Value> filteredValues = PercentileFilter.filter( groupedRuns, params.getPercentile() );
        filteredValues = FailureFilter.filter( filteredValues, params.getFailureType() );

        ArrayList<Series> series = new ArrayList<Series>();
        series.add(new Series(SeriesBuilder.toPoints(filteredValues, 1)));

        return new Chart(series);
    }
}
