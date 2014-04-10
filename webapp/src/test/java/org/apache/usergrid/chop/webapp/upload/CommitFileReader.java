package org.apache.usergrid.chop.webapp.upload;

import org.safehaus.chop.api.Commit;
import org.apache.usergrid.chop.webapp.dao.model.BasicCommit;

import java.text.SimpleDateFormat;
import java.util.Properties;

public class CommitFileReader {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    public static Commit read(String filePath) throws Exception {

        Properties props = FileUtil.readProperties(filePath);

        return new BasicCommit(
                props.getProperty("git.uuid"),
//                ModuleFileReader.read(filePath),
                null,
                props.getProperty("war.md5"),
                DATE_FORMAT.parse( props.getProperty("create.timestamp")),
                props.getProperty( "runnerPath" ));
    }
}
