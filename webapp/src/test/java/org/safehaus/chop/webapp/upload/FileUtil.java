package org.safehaus.chop.webapp.upload;


import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class FileUtil {

    public static Properties readProperties(String filePath) {

        Properties props = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("<path-to-project.properties>");
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
            Object obj = new JSONParser().parse( new FileReader( filePath ) );
            json = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

}
