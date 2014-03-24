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
package org.safehaus.chop.webapp;


import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.safehaus.chop.webapp.dao.SetupDao;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchFig;
import org.safehaus.chop.webapp.elasticsearch.EsEmbedded;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.shiro.MyShiroWebModule;
import org.safehaus.guicyfig.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.cli.CommandLine;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;

import static org.safehaus.chop.webapp.ChopUiFig.*;


/** ... */
@SuppressWarnings( "UnusedDeclaration" )
public class ChopUiConfig extends GuiceServletContextListener {

    private final static Logger LOG = LoggerFactory.getLogger( ChopUiConfig.class );

    private EsEmbedded esEmbedded;
    private Injector injector;
    private ServletContext context;


    @Override
    protected Injector getInjector() {

        if ( injector != null ) {
            return injector;
        }

        injector = Guice.createInjector( new MyShiroWebModule( context ), new ChopUiModule() );
        InjectorFactory.setInjector( injector );

        return injector;
    }


    @Override
    public void contextInitialized( ServletContextEvent servletContextEvent ) {
        context = servletContextEvent.getServletContext();
        context.setAttribute( Injector.class.getName(), getInjector() );

        Injector injector = getInjector();
        ElasticSearchFig elasticSearchFig = injector.getInstance( ElasticSearchFig.class );
        ChopUiFig chopUiFig = injector.getInstance( ChopUiFig.class );



        /*
         * --------------------------------------------------------------------
         * Archaius Configuration Settings
         * --------------------------------------------------------------------
         */

        ConcurrentCompositeConfiguration ccc = new ConcurrentCompositeConfiguration();
        Env env = Env.getEnvironment();

        if ( env == Env.ALL ) {
            ConfigurationManager.getDeploymentContext().setDeploymentEnvironment( "PROD" );
            LOG.info( "Setting environment to: PROD" );



            /*
             * --------------------------------------------------------------------
             * Extract Configuration Settings from CommandLine
             * --------------------------------------------------------------------
             */

            if ( ChopUiLauncher.getCommandLine() != null ) {
                CommandLine cl = ChopUiLauncher.getCommandLine();

                if ( cl.hasOption( 'e' ) ) {
                    LOG.info( "The -e option has been provided: launching embedded elasticsearch instance." );

                    // This will set the parameters needed in the fig to attach to the embedded instance
                    esEmbedded = new EsEmbedded( elasticSearchFig );
                    esEmbedded.start();
                }
            }
            else {
                LOG.warn( "ChopUi not started via Launcher - no command line argument processing will take place." );
            }
        }
        else if ( env == Env.UNIT ) {
            LOG.info( "Operating in UNIT environment" );
        }

        ConfigurationManager.install( ccc );

        /*
         * --------------------------------------------------------------------
         * Environment Based Configuration Property Adjustments
         * --------------------------------------------------------------------
         */

        if ( LOG.isDebugEnabled() ) {
            Enumeration<String> names = context.getAttributeNames();
            LOG.debug( "Dumping attribute names: " );
            while ( names.hasMoreElements() ) {
                String name = names.nextElement();
                LOG.debug( "attribute {} = {}", name, context.getAttribute( name ) );
            }
        }

        // Checking if a temp directory is defined - usually null
        String contextTempDir = ( String ) context.getAttribute( CONTEXT_TEMPDIR_KEY );
        LOG.info( "From servlet context: {} = {}", CONTEXT_TEMPDIR_KEY, contextTempDir );

        if ( contextTempDir == null ) {
            LOG.info( "From ChopUiFig {} = {}", CONTEXT_TEMPDIR_KEY, chopUiFig.getContextTempDir() );
        }

        // Works with with the local ES but fails with the embedded ES
        setupStorage();
    }

    private void setupStorage() {
        LOG.info("Setting up the storage...");

        IElasticSearchClient esClient = getInjector().getInstance( IElasticSearchClient.class );
        SetupDao setupDao = getInjector().getInstance( SetupDao.class );

        LOG.info("esClient: {}", esClient);
        LOG.info("setupDao: {}", setupDao);

        try {
            setupDao.setup();
        } catch (Exception e) {
            LOG.error( "Failed to setup the storage!", e );
        }
    }

    @Override
    public void contextDestroyed( final ServletContextEvent servletContextEvent ) {
        super.contextDestroyed( servletContextEvent );

        if ( esEmbedded != null ) {
            esEmbedded.stop();
        }
    }
}

