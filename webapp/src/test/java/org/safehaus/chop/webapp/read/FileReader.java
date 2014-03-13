package org.safehaus.chop.webapp.read;

import org.safehaus.chop.api.Version;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FileReader {

    public static Properties readProperties(String filePath) {

        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("d:\\temp\\chop-data\\perftest-bucket-2\\tests\\2c6e5647\\project.properties");
            prop.load(input);
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

        return prop;
    }
}
