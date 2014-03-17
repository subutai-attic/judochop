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

import java.util.*;

public class MyShiroRealm extends AuthorizingRealm {

    public static final String FILENAME = "shiro.ini";

    private static Map<String, String> users = new HashMap<String, String>();
    private static Map<String, Set<String>> userRoles = new HashMap<String, Set<String>>();

    static {
        initRealm();
    }

    public static void initRealm() {
        List<String> lines = FileUtil.readFile(FILENAME);
        for (String line : lines) {
            String[] user = line.split(";");
            users.put(user[0], user[1]);

            String[] group = user[2].split(",");
            userRoles.put(user[0], new HashSet<String>(Arrays.asList(group)));
        }
    }

    public static void saveRealm(){
        StringBuilder lines = new StringBuilder();

        for(String username : users.keySet()){
            lines.append(username);
            lines.append(";");
            lines.append(users.get(username));
            lines.append(";");

            if(getUserRoles(username) != null){
                for(String group : getUserRoles(username)){
                    lines.append(group);
                    lines.append(",");
                }
            }

            lines.append(";location");
            lines.append(System.lineSeparator());
        }

        FileUtil.writeFile(FILENAME, lines.toString());
        initRealm();
    }

    public static boolean authUser(String username, String password) {
        try {
            if (username == null) {
                throw new AuthenticationException("Authentication failed");
            }

            String pwd = users.get(username.toLowerCase());
            if (pwd == null || !pwd.equalsIgnoreCase(password)) {
                throw new AuthenticationException("Authentication failed");
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static Map<String, String> getUsers() {
        return users;
    }

    public static Set<String> getUserRoles(String username) {
        if (username != null) {
            return userRoles.get(username.toLowerCase());
        }
        return null;
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

        String pwd = users.get(username.toLowerCase());
        if (pwd == null || !pwd.equalsIgnoreCase(password)) {
            throw new AuthenticationException("Authentication failed");
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
        for (String username : principalsList) {
            Set<String> uroles = userRoles.get(username.toLowerCase());
            if (uroles != null) {
                roles.addAll(uroles);
            }
        }

        return new SimpleAuthorizationInfo(roles);
    }
}
