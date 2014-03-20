package org.safehaus.chop.webapp.view.chart.builder;

import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.service.calc.overview.OverviewCollector;
import org.safehaus.chop.webapp.view.chart.overview.OverviewFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;

public class OverviewChartBuilder extends ChartBuilder {

    private CommitDao commitDao = InjectorFactory.getInstance(CommitDao.class);
    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);

    public String getChart(Params params) {

        List<Commit> commits = commitDao.getByModule( params.getModuleId() );
        List<Run> list = runDao.getList( commits, params.getTestName() );

        OverviewCollector collector = new OverviewCollector(commits, params.getMetricType(), params.getPercentile(), params.getFailureValue());

        for (Run run : list) {
            collector.collect(run);
        }

        OverviewFormat format = new OverviewFormat(collector);

        String s = FileUtil.getContent("js/overview-chart.js");
        s = s.replace( "$categories", format.getCategories() );
        s = s.replace( "$series", format.getSeries() );

        return s;
    }

}
