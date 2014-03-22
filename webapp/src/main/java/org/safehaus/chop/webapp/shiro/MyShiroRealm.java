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

/**
 *
 * @author dilshat
 */

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.safehaus.chop.webapp.dao.ProviderParamsDao;
import org.safehaus.chop.webapp.dao.UserDao;
import org.safehaus.chop.webapp.dao.model.BasicProviderParams;
import org.safehaus.chop.webapp.dao.model.User;
import org.safehaus.chop.webapp.service.InjectorFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MyShiroRealm extends AuthorizingRealm {

    // InjectorFactory.getInstance(ProviderParamsDao.class);
    public static boolean authUser(String username, String password) {
        try {
            if (username == null) {
                throw new AuthenticationException("Authentication failed");
            }

            System.out.println("authUser");

            if(username.equalsIgnoreCase("user") && password.equals("pass")){
                InjectorFactory.getInstance(UserDao.class).save(new User("user", "pass"));
                InjectorFactory.getInstance(ProviderParamsDao.class).save(new BasicProviderParams("user", "", "", "", "", "",
                        "", "", ""));
            } else {
                User user = InjectorFactory.getInstance(UserDao.class).get(username.toLowerCase());
                if (user == null || user.getPassword() == null || !user.getPassword().equalsIgnoreCase(password)) {
                    throw new AuthenticationException("Authentication failed");
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public MyShiroRealm() {
        super(new MemoryConstrainedCacheManager(), new SimpleCredentialsMatcher());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        String username = upToken.getUsername();
        String password = String.valueOf(upToken.getPassword());
        if (username == null) {
            throw new AuthenticationException("Authentication failed");
        }

        System.out.println("doGetAuthenticationInfo");
        if(username.equals(username) && password.equals("pass")){
            try {
                InjectorFactory.getInstance(UserDao.class).save(new User("user", "pass"));
                InjectorFactory.getInstance(ProviderParamsDao.class).save(new BasicProviderParams("user", "", "", "", "", "",
                        "", "", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            User user = InjectorFactory.getInstance(UserDao.class).get(username.toLowerCase());
            if (user == null || user.getPassword() == null || !user.getPassword().equalsIgnoreCase(password)) {
                throw new AuthenticationException("Authentication failed");
            }
        }

        return new SimpleAuthenticationInfo(username, password, this.getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        Collection<String> principalsList = principals.byType(String.class);

        if (principalsList.isEmpty()) {
            throw new AuthorizationException("Empty principals list!");
        }

        Set<String> roles = new HashSet<String>();
        roles.add("temp");

        return new SimpleAuthorizationInfo(roles);
    }
}
