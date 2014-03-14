package org.safehaus.chop.webapp.upload;

import java.io.*;
import java.util.Properties;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FileUtil {

    public static Properties readProperties(String filePath) {

        Properties props = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("d:\\temp\\chop-data\\perftest-bucket-2\\tests\\2c6e5647\\project.properties");
            props.load(input);
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

        return props;
    }

    public static JSONObject readJson(String filePath) {

        JSONObject json = null;

        try {
            Object obj = new JSONParser().parse(new FileReader(filePath));
            json = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

}
