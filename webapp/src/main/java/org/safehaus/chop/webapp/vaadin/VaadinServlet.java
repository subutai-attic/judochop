package org.safehaus.chop.webapp.vaadin;

import com.google.inject.Singleton;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.*;

@Singleton
public class VaadinServlet extends com.vaadin.server.VaadinServlet {

    @Override
    public void init(final ServletConfig servletConfig) throws ServletException {

        final ArrayList list = new ArrayList();
        list.add("UI");

        final Hashtable<String, String> ht = new Hashtable<String, String>();
        ht.put("UI", "org.safehaus.chop.webapp.vaadin.TestUI");

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
//                return "org.safehaus.chop.webapp.vaadin.AddressbookUI";
                return ht.get(s);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                System.out.println(">> getInitParameterNames()");
//                return servletConfig.getInitParameterNames();
//                return list.iterator();
//                return new StringTokenizer("UI");
                return ht.keys();
            }
        };

        super.init(servletConfig_);
    }
}
