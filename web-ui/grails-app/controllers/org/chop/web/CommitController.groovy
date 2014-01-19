package org.chop.web

import groovy.json.JsonBuilder
import org.apache.commons.lang.StringUtils
import org.chop.service.data.Group
import org.chop.service.data.PointType
import org.chop.service.data.Storage
import org.chop.service.metric.Metric
import org.chop.web.util.Format
import org.chop.web.util.FormatCommit

class CommitController {

    private static final JsonBuilder BUILDER = new JsonBuilder()

    def index() {

        int percentile = StringUtils.isEmpty(params.percentile) ? 100 : Integer.parseInt(params.percentile)
        PointType pointType = StringUtils.isEmpty(params.pointFilter) ? PointType.ALL : (params.pointFilter as PointType)

        List<Map> jsonList = Storage.findByCommitAndClass(params.commitId, session.className)

        Map<Integer, Metric> runMap = Group.byRun(jsonList, session.metricType, percentile, pointType)

        String series = FormatCommit.format(runMap)

        render(view: "/commit", model: [series: series, runInfo: getRunInfo(jsonList) ])
    }

    private static String getRunInfo(List<Map> jsonList) {

        // <runNumber, runners>
        Map<String, Map> runnersJson = [:]

        jsonList.each { json ->
            // <runner, json>
            Map<String, Map> run = runnersJson.get(json.runNumber, [:])
            run.put(json.runner, json)
        }

        BUILDER(runnersJson)
        return BUILDER.toString()
    }
}
