package org.safehaus.chop.webapp.view.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.VaadinServletService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.*;

@Singleton
public class VaadinServlet extends com.vaadin.server.VaadinServlet {

    private static final String PARAM_UI = "UI";
    private static final String MAIN_UI = "org.safehaus.chop.webapp.view.MainUI";
    private static final Hashtable<String, String> PARAMS = getInitParams();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(getServletConfig(servletConfig));
    }

    private static Hashtable<String, String> getInitParams() {

        Hashtable<String, String> ht = new Hashtable<String, String>();
        ht.put(PARAM_UI, MAIN_UI);

        return ht;
    }

    private static ServletConfig getServletConfig(final ServletConfig servletConfig) {
        return new ServletConfig() {
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
                return PARAMS.get(s);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return PARAMS.keys();
            }
        };
    }
}
