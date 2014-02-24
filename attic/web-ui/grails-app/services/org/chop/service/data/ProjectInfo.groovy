package org.chop.service.data

import groovy.json.JsonBuilder
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.chop.service.metric.Metric
import org.chop.service.metric.MetricFactory
import org.chop.service.metric.MetricType

class ProjectInfo {

    private static final JsonBuilder BUILDER = new JsonBuilder()

    // <commitId, git.uuid>
    private static final Map<String, String> commitMap = [:]

    static Properties PROPS

    static void handle(String commitId, Properties props) {

        if (PROPS == null) {
            PROPS = props
        }

        commitMap.put(commitId, props.getProperty("git.uuid"))
    }

    static String getCommitInfo() {
        BUILDER(commitMap)
        return BUILDER.toString()
    }
}
