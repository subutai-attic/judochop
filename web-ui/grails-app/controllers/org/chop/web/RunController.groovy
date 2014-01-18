package org.chop.web

import org.chop.service.store.ResultStore
import org.chop.web.util.FormatRunner

class RunController {

    def index() {

        List<Map> jsonList = ResultStore.get(session.className, params.commitId, params.runNumber)

        String series = ""

        jsonList.each { json ->

            if (series != "") {
                series += ","
            }

            series += FormatRunner.format(json.runner, json.runResults)
        }

        render(view: "/run", model: [series: series])
    }
}
