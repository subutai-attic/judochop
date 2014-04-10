package org.apache.usergrid.chop.webapp.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.Scanner;

public class FileUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class.getName());
    private static URLClassLoader classLoader;

    public static Properties readProperties(String filePath) {

        Properties props = new Properties();
        String content = FileUtil.getContent(filePath);

        try {
            props.load( new StringReader(content) );
        } catch (IOException e) {
            LOG.error("Error to read properties file: ", e);
        }

        return props;
    }

    public static String getContent(String filePath) {
        String content = "";

        try {
            content = readFile(filePath);
        } catch (Exception e) {
            LOG.error("Error while reading file: " + e);
        }

        return content;
    }

    private static String readFile(String filePath) throws IOException {

        InputStream is = getClassLoader().getResourceAsStream(filePath);
        String s = streamToString(is);
        is.close();

        return s;
    }

    private static URLClassLoader getClassLoader() {

        if (classLoader != null) {
            return classLoader;
        }

        // Needed an instance to get URL b/c the static way doesn't work - FileUtil.class.getClass().
        URL url = new FileUtil().getClass().getProtectionDomain().getCodeSource().getLocation();
        classLoader = new URLClassLoader(new URL[]{ url }, Thread.currentThread().getContextClassLoader());

        return classLoader;
    }

    private static String streamToString(InputStream is) {
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
