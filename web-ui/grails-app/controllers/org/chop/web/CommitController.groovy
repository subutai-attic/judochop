package org.chop.web

import org.apache.commons.lang.StringUtils
import org.chop.service.data.Group
import org.chop.service.data.PointType
import org.chop.service.data.Storage
import org.chop.service.metric.Metric
import org.chop.web.util.Format
import org.chop.web.util.FormatCommit

class CommitController {

    def index() {

        int percentile = StringUtils.isEmpty(params.percentile) ? 100 : Integer.parseInt(params.percentile)
        PointType pointType = StringUtils.isEmpty(params.pointFilter) ? PointType.ALL : (params.pointFilter as PointType)

        List<Map> jsonList = Storage.findByCommitAndClass(params.commitId, session.className)

        Map<Integer, Metric> runMap = Group.byRun(jsonList, session.metricType, percentile, pointType)

        String series = FormatCommit.format(runMap)

        render(view: "/commit", model: [series: series])
    }
}
