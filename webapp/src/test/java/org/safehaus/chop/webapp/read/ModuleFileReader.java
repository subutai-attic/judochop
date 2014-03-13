package org.safehaus.chop.webapp.read;

import org.safehaus.chop.api.Module;
import org.safehaus.chop.webapp.dao.model.BasicModule;

import java.util.Properties;

public class ModuleFileReader {

    public static Module read(String filePath) {

        Properties props = FileReader.readProperties(filePath);

        return new BasicModule(
                props.getProperty("group.id"),
                props.getProperty("artifact.id"),
                props.getProperty("project.version"),
                props.getProperty("git.url"),
                props.getProperty("test.package.base")
        );
    }
}
