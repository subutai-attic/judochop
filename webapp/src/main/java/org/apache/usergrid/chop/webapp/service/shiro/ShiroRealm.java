/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.usergrid.chop.webapp.service.shiro;

import org.apache.usergrid.chop.webapp.dao.ProviderParamsDao;
import org.apache.usergrid.chop.webapp.dao.UserDao;
import org.apache.usergrid.chop.webapp.dao.model.BasicProviderParams;
import org.apache.usergrid.chop.webapp.service.InjectorFactory;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.usergrid.chop.stack.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ShiroRealm extends AuthorizingRealm {

    private static final Logger LOG = LoggerFactory.getLogger(ShiroRealm.class);

    private static final String DEFAULT_USER = "user";
    private static final String DEFAULT_PASSWORD = "pass";

    public ShiroRealm() {
        super(new MemoryConstrainedCacheManager(), new SimpleCredentialsMatcher());
    }

    public static boolean authUser(String username, String password) {
        try {
            if (username == null) {
                throw new AuthenticationException("Authentication failed");
            }

            LOG.info("username: {}", username);

            if (username.equalsIgnoreCase("user") && password.equals("pass")) {
//                InjectorFactory.getInstance(UserDao.class).save( new User(DEFAULT_USER, DEFAULT_PASSWORD) );
//                InjectorFactory.getInstance(ProviderParamsDao.class).save( new BasicProviderParams("user") );
                initUserData();
            } else {
                User user = InjectorFactory.getInstance(UserDao.class).get(username.toLowerCase());
                if (user == null || user.getPassword() == null || !user.getPassword().equalsIgnoreCase(password)) {
                    throw new AuthenticationException("Authentication failed");
                }
            }

            return true;
        } catch (Exception e) {
            LOG.error("Error while authentication: ", e);
        }
        return false;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        String password = String.valueOf(token.getPassword());

        if (username == null) {
            throw new AuthenticationException("Authentication failed");
        }

        LOG.info("username: {}", username);

        if (username.equals(username) && password.equals("pass")) {
            try {
//                InjectorFactory.getInstance(UserDao.class).save( new User(DEFAULT_USER, DEFAULT_PASSWORD) );
//                InjectorFactory.getInstance(ProviderParamsDao.class).save( new BasicProviderParams("user") );
                initUserData();
            } catch (Exception e) {
                LOG.error("Error while authentication: ", e);
            }
        } else {
            User user = InjectorFactory.getInstance(UserDao.class).get(username.toLowerCase());
            if (user == null || user.getPassword() == null || !user.getPassword().equalsIgnoreCase(password)) {
                throw new AuthenticationException("Authentication failed");
            }
        }

        return new SimpleAuthenticationInfo(username, password, this.getName());
    }

    private static void initUserData() throws Exception {

        UserDao userDao = InjectorFactory.getInstance(UserDao.class);
        User user = userDao.get(DEFAULT_USER);

        if (user != null){
            return;
        }

        InjectorFactory.getInstance(UserDao.class).save( new User(DEFAULT_USER, DEFAULT_PASSWORD) );
        InjectorFactory.getInstance(ProviderParamsDao.class).save( new BasicProviderParams(DEFAULT_USER) );
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
