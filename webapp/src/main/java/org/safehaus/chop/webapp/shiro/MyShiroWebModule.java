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

import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.apache.shiro.guice.web.ShiroWebModule;

import javax.servlet.ServletContext;

/**
 *
 * @author dilshat
 */
public class MyShiroWebModule extends ShiroWebModule {

    public MyShiroWebModule(ServletContext sc) {
        super(sc);
    }

    @Override
    protected void configureShiroWeb() {

        addFilterChain("/logout", LOGOUT);
        ///////custom urls to monitor***************
//        addFilterChain("/test/*", AUTHC_BASIC);
        addFilterChain("/VAADIN/*", AUTHC_BASIC);

        //REST
        Key restFilter = Key.get(RestFilter.class);

        addFilterChain("/verify/**", restFilter);
        ///////***************
//        addFilterChain("/**", AUTHC);
        bindRealm().to(MyShiroRealm.class).in(Singleton.class);
        bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/login.jsp");
        bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(3600000L);//1 hour
        bindConstant().annotatedWith(Names.named("shiro.usernameParam")).to("user");
        bindConstant().annotatedWith(Names.named("shiro.passwordParam")).to("pass");
//              bindConstant().annotatedWith(Names.named("shiro.rememberMeParam")).to("remember");
        bindConstant().annotatedWith(Names.named("shiro.successUrl")).to("/index.jsp");
        bindConstant().annotatedWith(Names.named("shiro.failureKeyAttribute")).to("shiroLoginFailure");
        bindConstant().annotatedWith(Names.named("shiro.unauthorizedUrl")).to("/denied.jsp");
//              bindConstant().annotatedWith(Names.named("shiro.port")).to(8443);

        bindConstant().annotatedWith(Names.named("shiro.redirectUrl")).to("/login.jsp");

    }

}
