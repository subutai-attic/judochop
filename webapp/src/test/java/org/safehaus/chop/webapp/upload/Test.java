package org.safehaus.chop.webapp.upload;

import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class Test {

    public static void main(String[] args) throws Exception {

        String moduleFile = "d:\\temp\\chop-data\\perftest-bucket-2\\tests\\2c6e5647\\project.properties";

        System.out.println(ModuleFileReader.read(moduleFile));

        /*Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("d:\\temp\\chop-data\\perftest-bucket-2\\tests\\2c6e5647\\project.properties");

            prop.load(input);

            System.out.println(prop.getProperty("group.id"));
            System.out.println(prop.getProperty("artifact.id"));
            System.out.println(prop.getProperty("project.version"));
            System.out.println(prop.getProperty("git.url"));
            System.out.println(prop.getProperty("test.package.base"));

        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

    }

    public static void mainJson(String[] args) throws Exception {

        JSONParser parser = new JSONParser();

        FileReader fr = new FileReader("d:\\temp\\chop-data\\perftest-bucket-2\\tests\\2c6e5647\\project.properties");

        Object obj = parser.parse(fr);

        //JSONObject jsonObject = (JSONObject) obj;

//        System.out.println(jsonObject);


        /*String name = (String) jsonObject.get("name");
        System.out.println(name);

        long age = (Long) jsonObject.get("age");
        System.out.println(age);

        // loop array
        JSONArray msg = (JSONArray) jsonObject.get("messages");
        Iterator<String> iterator = msg.iterator();

        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }*/

    }
}
