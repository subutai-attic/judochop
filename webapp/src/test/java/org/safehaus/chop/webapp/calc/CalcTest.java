package org.safehaus.chop.webapp.calc;

import com.google.inject.Inject;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.calc.OverviewAvg;
import org.safehaus.chop.webapp.service.calc.OverviewCollector;
import org.safehaus.chop.webapp.service.metric.Metric;
import org.safehaus.chop.webapp.view.chart.format.OverviewFormat;

import java.util.*;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class CalcTest {

    @Inject
    private CommitDao commitDao;

    @Inject
    private RunDao runDao;

    @Test
    public void test() throws Exception {

        OverviewCollector collector = new OverviewCollector( commitDao.getByModule("1168044208") );
        List<Run> list = runDao.getAll();

        for (Run run : list) {
            collector.collect(run);
        }

        System.out.println(collector);

        OverviewFormat format = new OverviewFormat(collector);

        System.out.println( format.getCategories() );
        System.out.println(format.getSeries());
    }

    @Test
    public void test2() throws Exception {

        OverviewCollector collector = new OverviewCollector( commitDao.getByModule("1168044208") );
        List<Run> list = runDao.getAll();

        for (Run run : list) {
            collector.collect(run);
        }

        System.out.println(collector);

        Map<String, Map<Integer, Metric>> values = collector.getValues();
        List<Double> arr = new ArrayList<Double>();

        for (Map<Integer, Metric> runs : values.values()) {
            for (Metric metric : runs.values()) {
                arr.add(metric.getValue());
            }
        }

        double arr2[] = new double[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            arr2[i] = arr.get(i);
        }

        double p = new DescriptiveStatistics(arr2).getPercentile(50);
        System.out.println("p: " + p);

        Map<String, Map<Integer, Metric>> filteredValues = new LinkedHashMap<String, Map<Integer, Metric>>();

        for (String commitId : values.keySet()) {
            Map<Integer, Metric> runs = values.get(commitId);

            for (Integer runNumber : runs.keySet()) {
                Metric metric = runs.get(runNumber);

                if (metric.getValue() > p) {
                    continue;
                }

                Map<Integer, Metric> filteredRuns = filteredValues.get( commitId );

                if (filteredRuns == null) {
                    filteredRuns = new HashMap<Integer, Metric>();
                    filteredValues.put(commitId, filteredRuns);
                }

                filteredRuns.put(runNumber, metric);
            }
        }

        System.out.println(filteredValues);

    }

    @Test
    public void test3() throws Exception {

        double[] arr = {3, 2, 3, 3, 3};
        double p = new DescriptiveStatistics(arr).getPercentile(50);

        System.out.println(p);

    }
}
