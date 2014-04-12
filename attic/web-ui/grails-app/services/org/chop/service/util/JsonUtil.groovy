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
package org.chop.service.util

import groovy.json.JsonSlurper
import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.LogFactory

class JsonUtil {

    private static def LOG = LogFactory.getLog(JsonUtil.class)
    private static final JsonSlurper JSON_SLURPER = new JsonSlurper()

    static Map parseFile(File file) {

        Map json = null

        try {
            json = JSON_SLURPER.parseText(file.text)
            json.putAll( getMetaData(file) )
        } catch (Exception e) {
            LOG.error("Exception while parsing a file: " + file, e)
        }

        return json
    }

    private static Map getMetaData(File file) {

        File runDir = file.parentFile.parentFile
        File commitDir = runDir.parentFile
        String runner = StringUtils.substringBeforeLast(file.name, "-")

        return [commitId: commitDir.name, runNumber: runDir.name, runner: runner]
    }
}
