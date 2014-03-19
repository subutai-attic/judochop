package org.safehaus.chop.webapp.view.util;

import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;

public class JavaScriptUtil {

    public static void loadFile(String fileName) {
        execute( FileUtil.getContent(fileName) );
    }

    public static void execute(String script) {
        JavaScript.getCurrent().execute(script);
    }

    public static void addCallback(String functionName, JavaScriptFunction callback) {
        JavaScript.getCurrent().addFunction(functionName, callback);
    }
}
