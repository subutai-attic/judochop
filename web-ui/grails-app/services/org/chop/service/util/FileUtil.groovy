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
