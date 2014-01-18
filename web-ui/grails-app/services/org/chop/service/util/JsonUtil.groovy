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
