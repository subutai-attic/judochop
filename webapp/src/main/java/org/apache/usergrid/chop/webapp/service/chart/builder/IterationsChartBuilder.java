package org.apache.usergrid.chop.webapp.service.chart.builder;

import com.google.inject.Inject;
import org.apache.usergrid.chop.webapp.dao.RunDao;
import org.apache.usergrid.chop.webapp.service.chart.Chart;
import org.apache.usergrid.chop.webapp.service.chart.Params;
import org.apache.usergrid.chop.webapp.service.chart.builder.average.IterationsAverage;
import org.apache.usergrid.chop.webapp.service.chart.filter.PercentileFilter;
import org.apache.usergrid.chop.webapp.service.chart.group.GroupByRunner;
import org.apache.usergrid.chop.webapp.service.chart.series.Series;
import org.apache.usergrid.chop.webapp.service.chart.series.SeriesBuilder;
import org.apache.usergrid.chop.webapp.service.chart.value.Value;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.apache.usergrid.chop.webapp.dao.RunResultDao;
import org.apache.usergrid.chop.webapp.service.chart.filter.FailureFilter;

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

        Map<String, Run> runs = runDao.getMap(params.getCommitId(), params.getRunNumber(), params.getTestName() );
        Map<Run, List<RunResult>> runResults = runResultDao.getMap(runs);

        Map<String, Collection<Value>> runnerValues = GroupByRunner.group(runResults);

        Map<String, Collection<Value>> filteredRunnerValues = PercentileFilter.filter(runnerValues, params.getPercentile());
        filteredRunnerValues = FailureFilter.filter(filteredRunnerValues, params.getFailureType() );

        List<Series> series = SeriesBuilder.toSeriesStaticX(filteredRunnerValues);
        series.add( new Series("AVG", SeriesBuilder.toPoints(IterationsAverage.calc(filteredRunnerValues), 1) ) );

        return new Chart(series);
    }
}
