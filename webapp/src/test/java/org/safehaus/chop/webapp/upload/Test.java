package org.safehaus.chop.webapp.upload;

import java.io.*;

public class Test {

    public static void main(String... args) {
        File[] files = new File("D:\\temp\\chop-data-upload").listFiles();
        showFiles(files);
    }

    public static void showFiles(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                showFiles(file.listFiles()); // Calls same method again.
            } else {
                System.out.println("File: " + file.getName());
            }
        }
    }
}
