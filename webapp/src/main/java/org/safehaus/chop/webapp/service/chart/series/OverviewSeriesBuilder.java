package org.safehaus.chop.webapp.service.chart.series;

import com.google.inject.Inject;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.chart.filter.PercentileFilter;
import org.safehaus.chop.webapp.service.chart.group.GroupByCommit;
import org.safehaus.chop.webapp.service.chart.dto.Params;
import org.safehaus.chop.webapp.service.chart.group.GroupByRunNumber;
import org.safehaus.chop.webapp.service.chart.value.RunValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OverviewSeriesBuilder {

    private CommitDao commitDao;
    private RunDao runDao;

    @Inject
    public OverviewSeriesBuilder(CommitDao commitDao, RunDao runDao) {
        this.commitDao = commitDao;
        this.runDao = runDao;
    }

    public void getSeries(Params params) {

        List<Commit> commits = commitDao.getByModule( params.getModuleId() );
        List<Run> runs = runDao.getList( commits, params.getTestName() );

        Map<String, List<Run>> commitRuns = new GroupByCommit(commits, runs).get();
        Map<String, Collection<RunValue>> groupedByRunNumber = groupByRunNumber(commitRuns);

        Map<String, Collection<RunValue>> resultMap = PercentileFilter.filter( groupedByRunNumber, params.getPercentile() );
        System.out.println(resultMap);
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
