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
import org.safehaus.chop.webapp.service.chart.value.RunValue;

import java.util.*;

public class OverviewChartBuilder_ extends ChartBuilder_ {

    private CommitDao commitDao;
    private RunDao runDao;

    @Inject
    public OverviewChartBuilder_(CommitDao commitDao, RunDao runDao) {
        this.commitDao = commitDao;
        this.runDao = runDao;
    }

    public Chart getChart(Params params) {

        List<Commit> commits = commitDao.getByModule( params.getModuleId() );
        List<Run> runs = runDao.getList( commits, params.getTestName() );

        Map<String, List<Run>> commitRuns = new GroupByCommit(commits, runs).get();
        Map<String, Collection<RunValue>> groupedByRunNumber = groupByRunNumber(commitRuns);

        Map<String, Collection<RunValue>> resultMap = PercentileFilter.filter( groupedByRunNumber, params.getPercentile() );
        resultMap = FailureFilter.filter( resultMap, params.getFailureValue() );

        List<Series> series = toSeries(resultMap);

        series.add( new Series( "Average", toPoints( getAvg(resultMap) ) ) );

        return new Chart(series, resultMap.keySet());
    }

    private static List<Point> toPoints(Collection<RunValue> values) {

        ArrayList<Point> points = new ArrayList<Point>();
        int x = 0;

        for (RunValue value : values) {
            points.add( new Point( x, value.getValue(), value.getFailures(), value.getProperties() ) );
            x++;
        }

        return points;
    }

    private static Collection<RunValue> getAvg(Map<String, Collection<RunValue>> commitRuns) {

        ArrayList<RunValue> avgValues = new ArrayList<RunValue>();

        for ( String commitId : commitRuns.keySet() ) {
            Collection<RunValue> values = commitRuns.get(commitId);
            avgValues.add( getAvg(values) );
        }

        return avgValues;
    }

    private static RunValue getAvg(Collection<RunValue> values) {

        RunValue avg = new RunValue();

        for (RunValue value : values) {
            avg.merge(value);
        }

        return avg;
    }

    private static List<Series> toSeries(Map<String, Collection<RunValue>> map) {

        ArrayList<Series> seriesList = new ArrayList<Series>();
        int x = 0;

        for ( String key : map.keySet() ) {
            Collection<RunValue> values = map.get(key);
            seriesList.add( new Series( toPoints(values, x) ) );
            x++;
        }

        return seriesList;
    }

    private static List<Point> toPoints(Collection<RunValue> values, int x) {

        ArrayList<Point> points = new ArrayList<Point>();

        for (RunValue value : values) {
            points.add( new Point( x, value.getValue(), value.getFailures(), value.getProperties() ) );
        }

        return points;
    }

    private static Map<String, Collection<RunValue>> groupByRunNumber(Map<String, List<Run>> commitRuns) {

        Map<String, Collection<RunValue>> grouped = new LinkedHashMap<String, Collection<RunValue>>();

        for ( String commitId : commitRuns.keySet() ) {
            List<Run> runs = commitRuns.get(commitId);
            grouped.put( commitId, new GroupByRunNumber(runs).get() );
        }

        return grouped;
    }

}
