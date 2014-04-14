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
