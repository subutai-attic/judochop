package org.safehaus.chop.webapp.service.chart.builder;

import com.google.inject.Inject;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.Point;
import org.safehaus.chop.webapp.service.chart.Series;
import org.safehaus.chop.webapp.service.chart.group.GroupByRunNumber;
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

        Collection<Value> groupedRuns = new GroupByRunNumber( runs, params.getMetricType() ).get();

        ArrayList<Series> seriesList = new ArrayList<Series>();
        seriesList.add( new Series( toPoints( groupedRuns )) );

        return new Chart(seriesList);
    }

    private static List<Point> toPoints(Collection<Value> values) {

        ArrayList<Point> points = new ArrayList<Point>();
        int x = 1;

        for (Value value : values) {
            points.add( new Point(x, value) );
            x++;
        }

        return points;
    }

}
