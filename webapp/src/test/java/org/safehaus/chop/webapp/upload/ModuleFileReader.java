package org.safehaus.chop.webapp.upload;

import org.safehaus.chop.api.Module;
import org.safehaus.chop.webapp.dao.model.BasicModule;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ModuleFileReader {

    public static Module read(String filePath) {

        Properties prop = new Properties();
        InputStream input = null;
        Module module = null;

        try {
            input = new FileInputStream(filePath);
            prop.load(input);

            module = new BasicModule(
                    prop.getProperty("group.id"),
                    prop.getProperty("artifact.id"),
                    prop.getProperty("project.version"),
                    prop.getProperty("git.url"),
                    prop.getProperty("test.package.base")
            );

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return module;
    }
}
