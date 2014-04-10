package org.apache.usergrid.chop.webapp.view.util;

import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import org.apache.usergrid.chop.webapp.service.util.FileUtil;

public class JavaScriptUtil {

    private static void execute(String script) {
        JavaScript.getCurrent().execute(script);
    }

    private static void addCallback(String jsCallbackName, JavaScriptFunction jsCallback) {
        JavaScript.getCurrent().addFunction(jsCallbackName, jsCallback);
    }

    public static void loadFile(String fileName) {
        execute( FileUtil.getContent(fileName) );
    }

    public static void loadChart(String chart, String jsCallbackName, JavaScriptFunction jsCallback) {
        execute(chart);
        addCallback(jsCallbackName, jsCallback);
    }
}
