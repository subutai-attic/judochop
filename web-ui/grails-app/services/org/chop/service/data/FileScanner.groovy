package org.chop.service.data

import groovy.json.JsonSlurper
import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.LogFactory

import javax.servlet.ServletContext

class FileScanner {

    private static def LOG = LogFactory.getLog(FileScanner.class)
    private static final JsonSlurper JSON_SLURPER = new JsonSlurper()
    static File dataDir
    private static final String FILE_NAME_SUFFIX = "-summary.json"

    private static final List<String> SCANNED_FILES = []

    static void setup(ServletContext ctx) {

        if (dataDir != null) {
            return
        }

        String configPath = ctx.getRealPath("WEB-INF/config.properties")
        Properties props = new Properties()
        File propsFile = new File(configPath)

        props.load(propsFile.newDataInputStream())

        dataDir = new File( props.get("dataDir") )
    }

    static List<String> updateStorage() {

        //Storage.clear()

        List<File> commitDirs = CommitOrder.get(dataDir)

        commitDirs.each { dir ->
            getSummaryFiles(dir).each { file ->
                handleFile(dir.name, file)
            }
        }

        return commitDirs.collect { "'$it.name'" }
    }

    private static void handleFile(String commitId, File file) {

        if (SCANNED_FILES.contains(file.absolutePath)) {
            return;
        }

        SCANNED_FILES.add(file.absolutePath)

        Map<String, String> json = JSON_SLURPER.parseText(file.text)
        json.put("commitId", commitId)

        String runner = StringUtils.substringBeforeLast(file.name, "-")
        json.put("runner", runner)

        Storage.add(commitId, json)

        LOG.info("Scanned file: " + file.absolutePath)
    }

    private static List<File> getSummaryFiles(File commitDir) {

        List<File> files = []

        commitDir.listFiles().each { file ->
            if (file.isDirectory()) {
                files.addAll(getSummaryFiles(file))
            } else if (file.getName().endsWith(FILE_NAME_SUFFIX)) {
                files.add(file)
            }
        }

        return files
    }
}
