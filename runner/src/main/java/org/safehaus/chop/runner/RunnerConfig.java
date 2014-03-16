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

import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.spi.RunnerRegistry;
import org.safehaus.chop.spi.Store;
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
public class RunnerConfig extends GuiceServletContextListener {
    private final static Logger LOG = LoggerFactory.getLogger( RunnerConfig.class );
    private Injector injector;
    private Store store;
    private RunnerRegistry registry;


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
        final Runner runner = injector.getInstance( Runner.class );
        final Project project = injector.getInstance( Project.class );
        ServletContext context = servletContextEvent.getServletContext();

        /*
         * --------------------------------------------------------------------
         * Adjust Runner Settings to Environment
         * --------------------------------------------------------------------
         */

        if ( env == Env.UNIT ) {
            runner.bypass( Runner.HOSTNAME_KEY, "localhost" );
            runner.bypass( Runner.IPV4_KEY, "127.0.0.1" );
        }
        else {
            Ec2Metadata.applyBypass( runner );
        }

        StringBuilder sb = new StringBuilder();
        sb.append( "https://" )
          .append( runner.getHostname() )
          .append( ':' )
          .append( runner.getServerPort() )
          .append( context.getContextPath() );
        String baseUrl = sb.toString();
        runner.bypass( Runner.URL_KEY, baseUrl );
        LOG.info( "Setting url key {} to base url {}", Runner.URL_KEY, baseUrl );

        File tempDir = new File( System.getProperties().getProperty( "java.io.tmpdir" ) );
        runner.bypass( Runner.RUNNER_TEMP_DIR_KEY, tempDir.getAbsolutePath() );
        LOG.info( "Setting runner temp directory key {} to context temp directory {}",
                Runner.RUNNER_TEMP_DIR_KEY, tempDir.getAbsolutePath() );

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
         * Start Up The Store
         * --------------------------------------------------------------------
         */

        if ( runner.getHostname() != null && project.getLoadKey() != null ) {
            registry = getInjector().getInstance( RunnerRegistry.class );
            store = getInjector().getInstance( Store.class );
            registry.start();
            store.start();
            LOG.info( "Store service started." );
            LOG.info( "RunnerRegistry service started." );

            registry.register( runner );
            LOG.info( "Registered runner information in store." );
        }
        else {
            LOG.warn( "Store not started, and not registered: insufficient configuration parameters." );
        }
    }


    @Override
    public void contextDestroyed( ServletContextEvent servletContextEvent ) {
        if ( store != null ) {
            registry.stop();
            store.stop();
        }
        super.contextDestroyed( servletContextEvent );
    }
}
