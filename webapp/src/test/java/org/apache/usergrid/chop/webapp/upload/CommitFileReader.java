package org.apache.usergrid.chop.webapp.upload;

<<<<<<< HEAD:webapp/src/test/java/org/safehaus/chop/webapp/upload/CommitFileReader.java
import org.apache.usergrid.chop.api.Commit;
import org.safehaus.chop.webapp.dao.model.BasicCommit;
=======
import org.safehaus.chop.api.Commit;
import org.apache.usergrid.chop.webapp.dao.model.BasicCommit;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:webapp/src/test/java/org/apache/usergrid/chop/webapp/upload/CommitFileReader.java

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
