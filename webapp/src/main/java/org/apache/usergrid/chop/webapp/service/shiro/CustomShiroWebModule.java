package org.apache.usergrid.chop.webapp.service.shiro;

import com.google.inject.Key;
import com.google.inject.Singleton;
import org.apache.shiro.guice.web.ShiroWebModule;

import javax.servlet.ServletContext;

@SuppressWarnings("unchecked")
public class CustomShiroWebModule extends ShiroWebModule {

    public CustomShiroWebModule(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    protected void configureShiroWeb() {

        addFilterChain("/logout", LOGOUT);
        addFilterChain("/VAADIN/*", AUTHC_BASIC);
        addFilterChain("/auth/**", Key.get(RestFilter.class));
        bindRealm().to(ShiroRealm.class).in(Singleton.class);

//        bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/login.jsp");
//        bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(3600000L);//1 hour
//        bindConstant().annotatedWith(Names.named("shiro.usernameParam")).to("user");
//        bindConstant().annotatedWith(Names.named("shiro.passwordParam")).to("pass");
//        bindConstant().annotatedWith(Names.named("shiro.successUrl")).to("/index.jsp");
//        bindConstant().annotatedWith(Names.named("shiro.failureKeyAttribute")).to("shiroLoginFailure");
//        bindConstant().annotatedWith(Names.named("shiro.unauthorizedUrl")).to("/denied.jsp");
//        bindConstant().annotatedWith(Names.named("shiro.redirectUrl")).to("/login.jsp");
    }
}
