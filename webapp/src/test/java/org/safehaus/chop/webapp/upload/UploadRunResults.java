package org.safehaus.chop.webapp.upload;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.model.BasicRun;

import java.io.File;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class UploadRunResults {

    private static final String DATA_DIR = "D:\\temp\\chop-data-upload";
    private static final String FILE_EXT = "results.json";
    private static final String COMMIT_ID = "cc471b502aca2791c3a068f93d15b79ff6b7b827";

    @Inject
    private RunDao runDao;

    @Test
    public void test() throws Exception {
        File[] files = new File(DATA_DIR).listFiles();
        loadFiles(files);
    }

    public void loadFiles(File[] files) throws Exception {
        for (File file : files) {
            if (file.isDirectory()) {
                loadFiles(file.listFiles());
            } else if (file.getName().endsWith(FILE_EXT)) {
                loadFile(file);
            }
        }
    }

    private void loadFile(File file) throws Exception {

        JSONObject json = FileUtil.readJson(file);

        String runner = StringUtils.substringBeforeLast(file.getName(), "-");
        String runNumber = file.getParentFile().getParentFile().getName();
        String testName = (String) json.get("testClass");

        BasicRun run = new BasicRun(
                COMMIT_ID, // commitId
                runner,
                Integer.parseInt(runNumber),
                testName
        );

        System.out.println(run.getId());

        /*boolean created = runDao.save(run);

        System.out.println(created + ": " + run );*/
    }


}
