package org.safehaus.chop.webapp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


public class UIServlet extends com.vaadin.server.VaadinServlet {

    @Override
    public void init(final ServletConfig servletConfig) throws ServletException {

        final ArrayList list = new ArrayList();
        list.add("UI");

        final Hashtable<String, String> ht = new Hashtable<String, String>();
        ht.put("UI", "kg.TestUI");

        ServletConfig servletConfig_ = new ServletConfig() {
            @Override
            public String getServletName() {
                return servletConfig.getServletName();
            }

            @Override
            public ServletContext getServletContext() {
                return servletConfig.getServletContext();
            }

            @Override
            public String getInitParameter(String s) {
                System.out.println(">> get param: " + s);
                return ht.get(s);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                System.out.println(">> getInitParameterNames()");
                return ht.keys();
            }
        };

        super.init(servletConfig_);
    }
}
