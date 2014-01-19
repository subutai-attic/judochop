package org.chop.service.data

class CommitOrder {

    private static final String PROPERTIES_FILE = "project.properties"
    private static final String CREATE_TIMESTAMP = "create.timestamp"

    public static List<File> get(File dir) {

        // <createTimestamp, commitId>
        TreeMap<String, File> orderMap = new TreeMap<String, File>()

        dir.listFiles().each {
            if (it.isDirectory()) {
                handle(orderMap, it)
            }
        }

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
