package org.chop.web

import org.apache.commons.lang.StringUtils
import org.chop.service.data.CommitCalc
import org.chop.service.data.FileScanner
import org.chop.service.data.Storage
import org.chop.service.metric.AggregatedMetric
import org.chop.service.metric.Metric
import org.chop.service.metric.MetricType
import org.chop.service.store.ResultFileScanner
import org.chop.service.store.ResultStore
import org.chop.web.util.Format

import javax.servlet.ServletContext

class MainController {

    private static boolean initDone = false

    private static void init(ServletContext ctx) {

        if (initDone) {
            return
        }

        FileScanner.setup(ctx)
        ResultFileScanner.update()
        initDone = true
    }

    def index() {

        init(session.getServletContext())

        List<String> commitDirs = FileScanner.updateStorage()
        Set<String> classNames = Storage.getClassNames()

        String className = getSelectedClassName(classNames)
        MetricType metricType = StringUtils.isEmpty(params.metric) ? MetricType.AVG : (params.metric as MetricType)
        int percentile = StringUtils.isEmpty(params.percentile) ? 100 : Integer.parseInt(params.percentile)

        setSessionParams(className, metricType)

        CommitCalc commitCalc = new CommitCalc(className, metricType, percentile)
        Map<String, List<Metric>> commits = commitCalc.get()

        int i = 0
        String str = ""

        commits.each { commitId, list ->

            if (str != "") {
                str += ","
            }

            str += Format.formatCommit(i, commitId, list)
            i++
        }

        str += "," + Format.formatValues( getMainValues(commits) )

        render(view: "/main-view", model: [commitDirs: commitDirs, classNames: classNames, series: str])
    }

    private void setSessionParams(String className, MetricType metricType) {
        session.className = className
        session.metricType = metricType
    }

    private String getSelectedClassName(Set<String> classNames) {
        return StringUtils.isEmpty(params.className) ? classNames.first() : params.className
    }

    private List<Metric> getMainValues(Map<String, List<Metric>> commits) {

        List<Metric> values = []

        commits.each { commitId, list ->
            AggregatedMetric aggr = new AggregatedMetric()

            list.each { metric ->
                aggr.add(metric)
            }

            values.add(aggr)
        }

        return values
    }
}
