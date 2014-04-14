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
