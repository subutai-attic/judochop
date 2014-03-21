package org.safehaus.chop.webapp.service.chart.builder;

import com.google.inject.Inject;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Point;
import org.safehaus.chop.webapp.service.chart.filter.FailureFilter;
import org.safehaus.chop.webapp.service.chart.filter.PercentileFilter;
import org.safehaus.chop.webapp.service.chart.group.GroupByCommit;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.group.GroupByRunNumber;
import org.safehaus.chop.webapp.service.chart.Series;
import org.safehaus.chop.webapp.service.chart.value.AvgValue;
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

        List<Commit> commits = commitDao.getByModule( params.getModuleId() );
        List<Run> runs = runDao.getList( commits, params.getTestName() );

        Map<String, List<Run>> commitRuns = new GroupByCommit(commits, runs).get();
        Map<String, Collection<Value>> groupedByRunNumber = groupByRunNumber( commitRuns, params.getMetricType() );

        Map<String, Collection<Value>> resultMap = PercentileFilter.filter( groupedByRunNumber, params.getPercentile() );
        resultMap = FailureFilter.filter( resultMap, params.getFailureValue() );

        List<Series> series = toSeries(resultMap);

        series.add( new Series( "Average", toPoints( getAvg(resultMap) ) ) );

        return new Chart(series, resultMap.keySet());
    }

    private static List<Point> toPoints(Collection<Value> values) {

        ArrayList<Point> points = new ArrayList<Point>();
        int x = 0;

        for (Value value : values) {
            points.add( new Point(x, value) );
            x++;
        }

        return points;
    }

    private static Collection<Value> getAvg(Map<String, Collection<Value>> commitRuns) {

        ArrayList<Value> avgValues = new ArrayList<Value>();

        for ( String commitId : commitRuns.keySet() ) {
            Collection<Value> values = commitRuns.get(commitId);
            avgValues.add( getAvg(values) );
        }

        return avgValues;
    }

    private static Value getAvg(Collection<Value> values) {

        Value avg = new AvgValue();

        for (Value value : values) {
            avg.merge(value);
        }

        return avg;
    }

    private static List<Series> toSeries(Map<String, Collection<Value>> map) {

        ArrayList<Series> seriesList = new ArrayList<Series>();
        int x = 0;

        for ( String key : map.keySet() ) {
            Collection<Value> values = map.get(key);
            seriesList.add( new Series( toPoints(values, x) ) );
            x++;
        }

        return seriesList;
    }

    private static List<Point> toPoints(Collection<Value> values, int x) {

        ArrayList<Point> points = new ArrayList<Point>();

        for (Value value : values) {
            points.add( new Point(x, value) );
        }

        return points;
    }

    private static Map<String, Collection<Value>> groupByRunNumber(Map<String, List<Run>> commitRuns, String metric) {

        Map<String, Collection<Value>> grouped = new LinkedHashMap<String, Collection<Value>>();

        for ( String commitId : commitRuns.keySet() ) {
            List<Run> runs = commitRuns.get(commitId);
            grouped.put( commitId, new GroupByRunNumber(runs, metric).get() );
        }

        return grouped;
    }

}
