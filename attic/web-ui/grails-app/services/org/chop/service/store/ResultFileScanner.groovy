package org.chop.service.store

import groovy.json.JsonSlurper
import org.apache.commons.logging.LogFactory
import org.chop.service.data.FileScanner
import org.chop.service.util.FileUtil
import org.chop.service.util.JsonUtil

class ResultFileScanner {

    private static def LOG = LogFactory.getLog(ResultFileScanner.class)
    private static final String FILE_NAME_SUFFIX = "-results.json"
    private static final List<String> SCANNED_FILES = []

    static void update() {

        //ResultStore.DATA.clear()

        List<File> files = FileUtil.recursiveAllFiles(FileScanner.dataDir, FILE_NAME_SUFFIX)

        parseFiles(files)
    }

    private static void parseFiles(List<File> files) {
        LOG.info("Parsing files: started")

        for (int i = 0; i < files.size(); i++) {
            parseFile(files[i])
        }

        LOG.info("Parsing files: completed")
    }

    private static void parseFile(File file) {

        if (SCANNED_FILES.contains(file.absolutePath)) {
            return;
        }

        SCANNED_FILES.add(file.absolutePath)

        Map json = JsonUtil.parseFile(file)

        if (json != null) {
            ResultStore.DATA.add(json)
        }

        LOG.info("Scanned file: " + file.absolutePath)
    }
}
