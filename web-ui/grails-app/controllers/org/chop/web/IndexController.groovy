package org.chop.web

import org.chop.service.data.CommitCalc

import org.chop.service.data.FileScanner
import org.chop.service.data.Storage
import org.chop.service.value.*

class IndexController {

    def index() {

        FileScanner.setup(session.getServletContext())

        List<String> commitDirs = FileScanner.updateStorage()
        Set<String> classNames = Storage.getClassNames()

        CommitCalc commitCalc = new CommitCalc(getSelectedClassName(classNames), params.metric)
        Map<String, List<Value>> commits = commitCalc.get()

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

        render(view: "/index", model: [commitDirs: commitDirs, classNames: classNames, series: str])
    }

    private String getSelectedClassName(Set<String> classNames) {
        return params.className != null ? params.className : classNames.first()
    }

    private List<Value> getMainValues(Map<String, List<Value>> commits) {

        List<Value> values = []

        commits.each { commitId, list ->
            AvgValue avg = new AvgValue()

            list.each { value ->
                avg.add(value)
            }

            values.add(avg)
        }

        return values
    }
}
