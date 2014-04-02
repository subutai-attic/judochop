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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.spi.RunnerRegistry;
import org.safehaus.chop.api.store.amazon.Ec2Metadata;
import org.safehaus.guicyfig.Env;
import org.safehaus.jettyjam.utils.TestMode;
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
    public static final String CHOP_IT_MODE = TestMode.TEST_MODE_PROPERTY;
    private Injector injector;


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

        if ( env == Env.UNIT || env == Env.INTEG || env == Env.ALL ) {
            runner.bypass( Runner.HOSTNAME_KEY, "localhost" );
            runner.bypass( Runner.IPV4_KEY, "127.0.0.1" );
        }
        else if ( env == Env.CHOP ) {
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
         * Start Up The RunnerRegistry and Register
         * --------------------------------------------------------------------
         */

         if ( System.getProperties().containsKey( CHOP_IT_MODE ) &&
              System.getProperty( CHOP_IT_MODE ).equalsIgnoreCase( "true" ) )
         {
             runner.bypass( Runner.HOSTNAME_KEY, "localhost" );
             runner.bypass( Runner.IPV4_KEY, "127.0.0.1" );
             project.bypass( Project.LOAD_KEY, "bogus-load-key" );
             project.bypass( Project.ARTIFACT_ID_KEY, "bogus-artifact-id" );
             project.bypass( Project.GROUP_ID_KEY, "org.safehaus.chop" );
             project.bypass( Project.CHOP_VERSION_KEY, "bogus-chop-version" );

             SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy.MM.dd.HH.mm.ss" );
             dateFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
             project.bypass( Project.CREATE_TIMESTAMP_KEY, dateFormat.format( new Date() ) );

             project.bypass( Project.GIT_URL_KEY, "http://stash.safehaus.org/projects/CHOP/repos/main/browse" );
             project.bypass( Project.GIT_UUID_KEY, "d637a8ce" );
             project.bypass( Project.LOAD_TIME_KEY, dateFormat.format( new Date() ) );
             project.bypass( Project.PROJECT_VERSION_KEY, "1.0.0-SNAPSHOT" );
         }

        if ( runner.getHostname() != null && project.getLoadKey() != null ) {
            RunnerRegistry registry = getInjector().getInstance( RunnerRegistry.class );

            if ( env == Env.CHOP ) {
                registry.register( runner );
                LOG.info( "Registered runner information in store." );
            }
            else {
                LOG.warn( "Env = {} so we are not registering this runner.", env );
            }

        }
        else {
            LOG.warn( "Runner registry not started, and not registered: insufficient configuration parameters." );
        }
    }


    public static boolean isTestMode() {
        return System.getProperties().containsKey( CHOP_IT_MODE ) && System.getProperty( CHOP_IT_MODE ).equalsIgnoreCase( "true" );
    }


    @Override
    public void contextDestroyed( ServletContextEvent servletContextEvent ) {
        super.contextDestroyed( servletContextEvent );
    }
}
