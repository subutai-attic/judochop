package org.safehaus.chop.webapp.service.chart.builder;

import com.google.inject.Inject;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.RunResultDao;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.Point;
import org.safehaus.chop.webapp.service.chart.Series;
import org.safehaus.chop.webapp.service.chart.filter.FailureFilter;
import org.safehaus.chop.webapp.service.chart.filter.PercentileFilter;
import org.safehaus.chop.webapp.service.chart.group.GroupByRunner;
import org.safehaus.chop.webapp.service.chart.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IterationsChartBuilder extends ChartBuilder {

    private RunDao runDao;
    private RunResultDao runResultDao;

    @Inject
    public IterationsChartBuilder(RunDao runDao, RunResultDao runResultDao) {
        this.runDao = runDao;
        this.runResultDao = runResultDao;
    }

    public Chart getChart(Params params) {

        Map<String, Run> runs = runDao.getMap( params.getCommitId(), params.getRunNumber(), params.getTestName() );
        Map<Run, List<RunResult>> runResults = runResultDao.getMap(runs);

        Map<String, Collection<Value>> runnerValues = GroupByRunner.group(runResults);

        Map<String, Collection<Value>> filteredRunnerValues = PercentileFilter.filter(runnerValues, params.getPercentile());
        filteredRunnerValues = FailureFilter.filter( filteredRunnerValues, params.getFailureValue() );

        List<Series> series = toSeries(filteredRunnerValues);

        series.add( new Series( "AVG", toPoints( IterationsAvg.get(filteredRunnerValues) ) ) );

        return new Chart(series);
    }

    private static List<Series> toSeries(Map<String, Collection<Value>> map) {

        ArrayList<Series> seriesList = new ArrayList<Series>();

        for ( String key : map.keySet() ) {
            Collection<Value> values = map.get(key);
            System.out.println(key + ": " + values);
            seriesList.add(new Series(key, toPoints(values)));
        }

        return seriesList;
    }

    private static <V extends Value> List<Point> toPoints(Collection<V> values) {

        ArrayList<Point> points = new ArrayList<Point>();
        int x = 1;

        for (Value value : values) {
            if (value != null) {
                points.add( new Point( x, value.getValue(), value.getFailures(), value.getProperties() ) );
            }
            x++;
        }

        return points;
    }


}
