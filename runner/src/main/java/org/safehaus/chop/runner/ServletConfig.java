/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.chop.runner;


import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequest;

import org.safehaus.chop.api.ProjectFig;
import org.safehaus.chop.api.RunnerFig;
import org.safehaus.chop.api.StoreService;
import org.safehaus.chop.api.store.amazon.Ec2Metadata;
import org.safehaus.guicyfig.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;


/** ... */
@SuppressWarnings( "UnusedDeclaration" )
public class ServletConfig extends GuiceServletContextListener {
    private final static Logger LOG = LoggerFactory.getLogger( ServletConfig.class );
    private Injector injector;
    private StoreService storeService;


    @Override
    protected Injector getInjector() {
        if ( injector != null ) {
            return injector;
        }

        injector = Guice.createInjector( new Module() );
        return injector;
    }


    @Override
    public void contextInitialized( ServletContextEvent servletContextEvent ) {
        super.contextInitialized( servletContextEvent );

        /*
         * --------------------------------------------------------------------
         * Archaius Configuration Settings
         * --------------------------------------------------------------------
         */

        ConcurrentCompositeConfiguration ccc = new ConcurrentCompositeConfiguration();
        Env env = Env.getEnvironment();

        if ( env == Env.ALL ) {
            ConfigurationManager.getDeploymentContext().setDeploymentEnvironment( "CHOP" );
            LOG.info( "Setting environment to: CHOP" );
        }
        else if ( env == Env.UNIT ) {
            LOG.info( "Operating in UNIT environment" );
        }

        ConfigurationManager.install( ccc );
        try {
            ConfigurationManager.loadCascadedPropertiesFromResources( "project" );
        }
        catch ( IOException e ) {
            LOG.error( "Failed to load project properties!", e );
            throw new RuntimeException( "Cannot do much without properly loading our configuration.", e );
        }

        /*
         * --------------------------------------------------------------------
         * Environment Based Configuration Property Adjustments
         * --------------------------------------------------------------------
         */

        final ServletFig servletFig = injector.getInstance( ServletFig.class );
        final RunnerFig runnerFig = injector.getInstance( RunnerFig.class );
        final ProjectFig projectFig = injector.getInstance( ProjectFig.class );
        ServletContext context = servletContextEvent.getServletContext();

        /*
         * --------------------------------------------------------------------
         * Adjust RunnerFig Settings to Environment
         * --------------------------------------------------------------------
         */

        Ec2Metadata.applyBypass( runnerFig );

        StringBuilder sb = new StringBuilder();
        sb.append( "https://" )
          .append( runnerFig.getHostname() )
          .append( ':' )
          .append( runnerFig.getServerPort() )
          .append( context.getContextPath() );
        String baseUrl = sb.toString();
        runnerFig.bypass( RunnerFig.URL_KEY, baseUrl );
        LOG.info( "Setting url key {} to base url {}", RunnerFig.URL_KEY, baseUrl );

        File tempDir = ( File ) context.getAttribute( ServletFig.CONTEXT_TEMPDIR_KEY );
        runnerFig.bypass( RunnerFig.RUNNER_TEMP_DIR_KEY, tempDir.getAbsolutePath() );
        LOG.info( "Setting runner temp directory key {} to context temp directory {}",
                RunnerFig.RUNNER_TEMP_DIR_KEY, tempDir.getAbsolutePath() );

        /*
         * --------------------------------------------------------------------
         * Adjust ServletFig Settings to Environment
         * --------------------------------------------------------------------
         */

        servletFig.bypass( ServletFig.SERVER_INFO_KEY, context.getServerInfo() );
        LOG.info( "Setting server info key {} to {}", ServletFig.SERVER_INFO_KEY, context.getServerInfo() );

        servletFig.bypass( ServletFig.CONTEXT_PATH, context.getContextPath() );
        LOG.info( "Setting server context path key {} to {}", ServletFig.CONTEXT_PATH, context.getContextPath() );

        // @todo Is this necessary?
        servletFig.bypass( ServletFig.CONTEXT_TEMPDIR_KEY, tempDir.getAbsolutePath() );
        LOG.info( "Setting runner context temp directory key {} to context temp directory {}",
                ServletFig.CONTEXT_TEMPDIR_KEY, tempDir.getAbsolutePath() );

        /*
         * --------------------------------------------------------------------
         * Start Up The StoreService
         * --------------------------------------------------------------------
         */

        if ( runnerFig.getHostname() != null && projectFig.getLoadKey() != null ) {
            storeService = getInjector().getInstance( StoreService.class );
            storeService.start();
            LOG.info( "Store service started." );

            storeService.register( runnerFig );
            LOG.info( "Registered runner information in store." );
        }
        else {
            LOG.warn( "Store not started, and not registered: insufficient configuration parameters." );
        }
    }


    @Override
    public void contextDestroyed( ServletContextEvent servletContextEvent ) {
        if ( storeService != null ) {
            storeService.stop();
        }
        super.contextDestroyed( servletContextEvent );
    }
}
