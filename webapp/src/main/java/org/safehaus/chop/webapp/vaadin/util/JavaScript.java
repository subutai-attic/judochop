package org.safehaus.chop.webapp.vaadin.util;

import com.vaadin.ui.Window;

public class JavaScript {

    private Window window;

    public JavaScript(Window window) {
        this.window = window;
    }

    public void execute(String code) {
        //window.executeJavaScript(code);
    }

    public void loadFile(String filePath) {
        execute(FileUtil.getContent(filePath));
    }
}
