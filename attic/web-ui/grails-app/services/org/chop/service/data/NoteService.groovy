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
