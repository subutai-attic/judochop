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
import org.chop.service.data.FileScanner
import org.chop.service.data.Storage
import org.chop.service.store.ResultStore

class FileUtil {

    static List<File> recursiveAllFiles(File dir, String fileNameSuffix) {

        if (!dir.isDirectory()) {
            return [dir]
        }

        List<File> files = []

        dir.listFiles().each { file ->
            if (file.isDirectory()) {
                files.addAll( recursiveAllFiles(file, fileNameSuffix) )
            } else if (file.getName().endsWith(fileNameSuffix)) {
                files.add(file)
            }
        }

        return files
    }
}
