/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.chop.web

import groovy.json.JsonBuilder
import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.LogFactory
import org.chop.service.data.CommitCalc
import org.chop.service.data.FileScanner
import org.chop.service.data.PointType
import org.chop.service.data.ProjectInfo
import org.chop.service.data.Storage
import org.chop.service.metric.AggregatedMetric
import org.chop.service.metric.Metric
import org.chop.service.metric.MetricType
import org.chop.service.store.ResultFileScanner
import org.chop.web.util.Format

import javax.servlet.ServletContext

class MainController {

    private static def LOG = LogFactory.getLog(MainController.class)

    private static void init(ServletContext ctx) {

        LOG.info("Initializing...");

        FileScanner.setup(ctx)
        ResultFileScanner.update()
    }

    def index() {
        try {
            handle()
        } catch (Exception e) {
            LOG.error(e.getMessage(), e)
        }
    }

    private def handle() {

        init(session.getServletContext())

        List<String> commitDirs = FileScanner.updateStorage()

        Set<String> classNames = Storage.getClassNames()

        String className = getSelectedClassName(classNames)
        MetricType metricType = StringUtils.isEmpty(params.metric) ? MetricType.AVG : (params.metric as MetricType)
        int percentile = StringUtils.isEmpty(params.percentile) ? 100 : Integer.parseInt(params.percentile)
        PointType pointType = StringUtils.isEmpty(params.pointFilter) ? PointType.ALL : (params.pointFilter as PointType)

        setSessionParams(className, metricType)

        CommitCalc commitCalc = new CommitCalc(className, metricType, percentile, pointType)
        Map<String, List<Metric>> commits = commitCalc.get()

        int i = 0
        String series = ""

        commits.each { commitId, list ->

            if (series != "") {
                series += ","
            }

            series += Format.formatCommit(i, commitId, list)
            i++
        }

        series += "," + Format.formatValues( getMainValues(commits) )

        render(view: "/main-view", model: [
                commitDirs: commitDirs,
                classNames: classNames,
                series: series
        ])
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
