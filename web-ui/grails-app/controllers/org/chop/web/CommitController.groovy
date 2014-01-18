package org.chop.web

import org.chop.service.data.Group
import org.chop.service.data.Storage
import org.chop.service.metric.Metric
import org.chop.web.util.Format
import org.chop.web.util.FormatCommit

class CommitController {

    def index() {

        List<Map> jsonList = Storage.findByCommitAndClass(params.commitId, session.className)

        Map<Integer, Metric> runMap = Group.byRun(jsonList, session.metricType)

        String series = FormatCommit.format(runMap)

        render(view: "/commit", model: [series: series])
    }
}
