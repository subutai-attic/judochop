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
package org.apache.usergrid.chop.plugin;


import org.apache.usergrid.chop.api.ChopUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;


@Mojo( name = "setup" )
public class SetupMojo extends MainMojo {

    protected SetupMojo( MainMojo mojo ) {
        this.username = mojo.username;
        this.password = mojo.password;
        this.endpoint = mojo.endpoint;
        this.certStorePassphrase = mojo.certStorePassphrase;
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.localRepository = mojo.localRepository;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
        this.runnerCount = mojo.runnerCount;
    }


    @Override
    public void execute() throws MojoExecutionException {
        initCertStore();

    }


    protected void initCertStore() {
        if ( ChopUtils.isStoreInitialized() ) {
            return;
        }

        try {
            if ( certStorePassphrase == null ) {
                ChopUtils.installCert( endpoint, 443, null );
            }
            else {
                ChopUtils.installCert( endpoint, 443, certStorePassphrase.toCharArray() );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
