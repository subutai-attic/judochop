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

class NoteService {

    // <commitId-runNumber, noteText>
    private static Map<String, String> notes = null

    static void save(String commitId, String runNumber, String note) {

        String path = FileScanner.dataDir.absolutePath + "/${commitId}/$runNumber/notes.txt"

        File file = new File(path)
        file.write(note)

        notes.put(getKey(commitId, runNumber), note)
    }

    static String get(String commitId, String runNumber) {

        if (notes == null) {
            readAll()
        }

        return notes.get(getKey(commitId, runNumber), "")
    }

    private static String getKey(String commitId, String runNumber) {
        return "$commitId-$runNumber"
    }

    private static void readAll() {

        notes = [:]
        List<File> files = FileUtil.recursiveAllFiles(FileScanner.dataDir, "notes.txt")

        files.each { file ->
            File runDir = file.parentFile
            File commitDir = runDir.parentFile
            String key = commitDir.name + "-" + runDir.name
            notes.put(key, file.text)
        }

        println notes
    }

}
