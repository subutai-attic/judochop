package org.safehaus.chop.webapp.service.chart.builder;

import com.google.inject.Inject;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.builder.average.OverviewAverage;
import org.safehaus.chop.webapp.service.chart.filter.FailureFilter;
import org.safehaus.chop.webapp.service.chart.filter.PercentileFilter;
import org.safehaus.chop.webapp.service.chart.group.GroupByCommit;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.group.GroupByRunNumber;
import org.safehaus.chop.webapp.service.chart.series.Series;
import org.safehaus.chop.webapp.service.chart.series.SeriesBuilder;
import org.safehaus.chop.webapp.service.chart.value.Value;

import java.util.*;

public class OverviewChartBuilder extends ChartBuilder {

    private CommitDao commitDao;
    private RunDao runDao;

    @Inject
    public OverviewChartBuilder(CommitDao commitDao, RunDao runDao) {
        this.commitDao = commitDao;
        this.runDao = runDao;
    }

    public Chart getChart(Params params) {

        List<Commit> commits = commitDao.getByModule(params.getModuleId() );
        List<Run> runs = runDao.getList( commits, params.getTestName() );

        Map<String, List<Run>> commitRuns = new GroupByCommit(commits, runs).get();
        Map<String, Collection<Value>> groupedByRunNumber = groupByRunNumber( commitRuns, params.getMetricType() );

        Map<String, Collection<Value>> resultMap = PercentileFilter.filter(groupedByRunNumber, params.getPercentile() );
        resultMap = FailureFilter.filter(resultMap, params.getFailureType() );

        List<Series> series = SeriesBuilder.toSeries(resultMap);
        series.add( new Series("Average", SeriesBuilder.toPoints(OverviewAverage.calc(resultMap), 0) ) );

        return new Chart(series, resultMap.keySet());
    }

    private static Map<String, Collection<Value>> groupByRunNumber(Map<String, List<Run>> commitRuns, String metric) {

        Map<String, Collection<Value>> grouped = new LinkedHashMap<String, Collection<Value>>();

        for ( String commitId : commitRuns.keySet() ) {
            List<Run> runs = commitRuns.get(commitId);
            grouped.put(commitId, new GroupByRunNumber(runs, metric).get() );
        }

        return grouped;
    }

}
