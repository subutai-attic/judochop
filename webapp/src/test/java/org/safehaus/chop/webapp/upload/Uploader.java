package org.safehaus.chop.webapp.upload;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.RunResultDao;
import org.safehaus.chop.webapp.dao.model.BasicRun;
import org.safehaus.chop.webapp.dao.model.BasicRunResult;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.io.File;
import java.util.Iterator;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class Uploader {

    private static final String DATA_DIR = "D:\\temp\\chop-data-upload";
//    private static final String COMMIT_ID = "cc471b502aca2791c3a068f93d15b79ff6b7b827";
    private static final String COMMIT_ID = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";

    @Inject
    private RunDao runDao;

    @Inject
    private RunResultDao runResultDao;

    @Test
    public void test() throws Exception {
        File[] files = new File(DATA_DIR).listFiles();
        loadFiles(files);
    }

    public void loadFiles(File[] files) throws Exception {
        for (File file : files) {
            if (file.isDirectory()) {
                loadFiles(file.listFiles());
            } else if (file.getName().endsWith("summary.json")) {
                loadFile(file);
            }
        }
    }

    private void loadFile(File file) throws Exception {

        JSONObject json = FileUtil.readJson(file.getAbsolutePath());
        String runId = saveRun(json, file);

        saveRunResults(file, runId);
    }

    private void saveRunResults(File file, String runId) throws Exception {

        String resultsFileName = file.getAbsolutePath().replace("summary", "results");
        JSONObject json = FileUtil.readJson(resultsFileName);

        if (json == null) {
            return;
        }

        JSONArray runResults = (JSONArray) json.get("runResults");
        Iterator<JSONObject> iterator = runResults.iterator();

        while (iterator.hasNext()) {
            JSONObject jsonResult = iterator.next();

            BasicRunResult runResult = new BasicRunResult(
                    runId,
                    Util.getInt(jsonResult, "runCount"),
                    Util.getInt(jsonResult, "runTime"),
                    Util.getInt(jsonResult, "ignoreCount"),
                    Util.getInt(jsonResult, "failureCount")
            );

            runResult.setFailures("" + jsonResult.get("failures"));

            runResultDao.save(runResult);
        }

        System.out.println("Saved runResults: " + runResults.size());
    }

    private String saveRun(JSONObject json, File file) throws Exception {

        String runner = StringUtils.substringBeforeLast(file.getName(), "-");

        BasicRun run = new BasicRun(
                COMMIT_ID,
                runner,
                Util.getInt(json, "runNumber"),
                Util.getString(json, "testName")
        );

        run.copyJson(json);

        boolean created = runDao.save(run);
        System.out.println(created + ": " + run);

        return run.getId();
    }
}
