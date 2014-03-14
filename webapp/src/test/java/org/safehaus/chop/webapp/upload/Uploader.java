package org.safehaus.chop.webapp.upload;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.SummaryDao;
import org.safehaus.chop.webapp.dao.model.BasicRun;
import org.safehaus.chop.webapp.dao.model.BasicSummary;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.io.File;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class Uploader {

    private static final String DATA_DIR = "D:\\temp\\chop-data-upload";
    private static final String COMMIT_ID = "cc471b502aca2791c3a068f93d15b79ff6b7b827";

    @Inject
    private RunDao runDao;

    @Inject
    private SummaryDao summaryDao;

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

        JSONObject json = FileUtil.readJson(file);
        String runId = saveRun(json, file);

        saveSummary(json, runId);
    }

    private void saveSummary(JSONObject json, String runId) throws Exception {

        BasicSummary summary = new BasicSummary(runId);
        summary.copyJson(json);

        boolean created = summaryDao.save(summary);

        System.out.println(created + ": " + summary);
    }

    private String saveRun(JSONObject json, File file) throws Exception {

        String runner = StringUtils.substringBeforeLast(file.getName(), "-");

        BasicRun run = new BasicRun(
                COMMIT_ID,
                runner,
                Util.getInt(json, "runNumber"),
                Util.getString(json, "testName")
        );

        boolean created = runDao.save(run);

        System.out.println(created + ": " + run);

        return run.getId();
    }
}
