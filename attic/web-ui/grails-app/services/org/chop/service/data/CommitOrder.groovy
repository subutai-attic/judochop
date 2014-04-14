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

import org.chop.service.util.FileUtil

class CommitOrder {

    private static final String PROPERTIES_FILE = "project.properties"
    private static final String CREATE_TIMESTAMP = "create.timestamp"

    public static List<File> get(File dir) {

        // <createTimestamp, commitId>
        TreeMap<String, File> orderMap = new TreeMap<String, File>()

        List<File> files = FileUtil.recursiveAllFiles(FileScanner.dataDir, PROPERTIES_FILE)

        files.each { file ->
            File commitDir = file.parentFile
            if (commitDir.isDirectory()) {
                handle(orderMap, commitDir)
            }
        }

        /*dir.listFiles().each {
            if (it.isDirectory()) {
                handle(orderMap, it)
            }
        }*/

        return orderMap.values().collect()
    }

    private static void handle(Map<String, File> orderMap, File dir) {

        File propFile = dir.listFiles().find { it.name.equals(PROPERTIES_FILE) }

        if (propFile == null) {
            return
        }

        Properties props = new Properties()
        props.load(propFile.newDataInputStream())

        orderMap.put( props.getProperty(CREATE_TIMESTAMP), dir)

        ProjectInfo.handle(dir.name, props)
    }
}
