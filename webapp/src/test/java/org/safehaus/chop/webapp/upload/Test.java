package org.safehaus.chop.webapp.upload;

import java.io.*;
import java.util.ArrayList;

public class Test {

    public static void main(String... args) {

        ArrayList<String> list = new ArrayList<String>();
        list.add("hello1");
        list.add("hello2");

        list.set(0, null);
        list.set(1, null);

        System.out.println(list);

    }


}
