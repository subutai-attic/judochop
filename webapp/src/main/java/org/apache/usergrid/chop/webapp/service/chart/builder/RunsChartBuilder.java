package org.apache.usergrid.chop.webapp.service.chart.builder;

import com.google.inject.Inject;
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
