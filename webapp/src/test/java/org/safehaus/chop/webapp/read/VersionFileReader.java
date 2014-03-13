package org.safehaus.chop.webapp.read;

import org.safehaus.chop.api.Version;
import org.safehaus.chop.webapp.dao.model.BasicVersion;

import java.text.SimpleDateFormat;
import java.util.Properties;

public class VersionFileReader {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    public static Version read(String filePath) throws Exception {

        Properties props = FileReader.readProperties(filePath);

        return new BasicVersion(
                props.getProperty("git.uuid"),
                ModuleFileReader.read(filePath),
                props.getProperty("war.md5"),
                DATE_FORMAT.parse( props.getProperty("create.timestamp") )
        );
    }
}
