package org.safehaus.chop.webapp.service.chart.builder;

import com.google.inject.Inject;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.Point;
import org.safehaus.chop.webapp.service.chart.series.Series;
import org.safehaus.chop.webapp.service.chart.filter.FailureFilter;
import org.safehaus.chop.webapp.service.chart.filter.PercentileFilter;
import org.safehaus.chop.webapp.service.chart.group.GroupByRunNumber;
import org.safehaus.chop.webapp.service.chart.series.SeriesBuilder;
import org.safehaus.chop.webapp.service.chart.value.Value;

import java.util.*;

public class RunsChartBuilder extends ChartBuilder {

    private RunDao runDao;

    @Inject
    public RunsChartBuilder(RunDao runDao) {
        this.runDao = runDao;
    }

    public Chart getChart(Params params) {

        List<Run> runs = runDao.getList( params.getCommitId(), params.getTestName() );

        Collection<Value> groupedRuns = new GroupByRunNumber(runs, params.getMetricType() ).get();

        Collection<Value> filteredValues = PercentileFilter.filter(groupedRuns, params.getPercentile() );
        filteredValues = FailureFilter.filter(filteredValues, params.getFailureType() );

        ArrayList<Series> series = new ArrayList<Series>();
        series.add(new Series(SeriesBuilder.toPoints(filteredValues, 1)));

        return new Chart(series);
    }
}
