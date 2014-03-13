package org.safehaus.chop.webapp.read;

import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class Test {


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
