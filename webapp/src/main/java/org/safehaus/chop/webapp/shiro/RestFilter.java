/*
 * Copyright 2014 dilshat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.safehaus.chop.webapp.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author dilshat
 */
public class RestFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request,
            ServletResponse response, Object mappedValue) {

        if (SecurityUtils.getSubject().isAuthenticated()) {
            return true;
        }
        String username = (request.getParameter("user"));
        String password = (request.getParameter("pwd"));
        if (MyShiroRealm.authUser(username, password)) {
            SecurityUtils.getSubject().login(new UsernamePasswordToken(username, password));
            return true;
        }
//        Subject subject = getSubject(request, response);
//        boolean isAuthenticated = subject.isAuthenticated();
//        return isAuthenticated;
        return false;
    }

    /**
     * Takes responsibility for returning an appropriate response when access is
     * not allowed.
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request,
            ServletResponse response) throws Exception {
        accessDeniedResponse(request, response);
        return false;
    }

    /**
     * Provides a 401 Not Authorized HTTP status code to the client with a
     * custom challenge scheme that the client understands and can respond to.
     *
     * @param request
     * @param response
     * @throws Exception
     */
    private void accessDeniedResponse(ServletRequest request,
            ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

}
