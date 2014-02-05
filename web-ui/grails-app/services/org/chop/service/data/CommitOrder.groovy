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
