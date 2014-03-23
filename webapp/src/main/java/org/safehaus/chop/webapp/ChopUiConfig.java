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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.safehaus.chop.webapp.elasticsearch.ElasticSearchFig;
import org.safehaus.chop.webapp.elasticsearch.EsEmbedded;
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

        /*
         * --------------------------------------------------------------------
         * Extract Configuration Settings from CommandLine
         * --------------------------------------------------------------------
         */

        if ( ChopUiLauncher.getCommandLine() != null ) {
            CommandLine cl = ChopUiLauncher.getCommandLine();

            if ( cl.hasOption( 'e' ) ) {
                esEmbedded = new EsEmbedded( elasticSearchFig );
                esEmbedded.start();
            }
        }


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

        ServletContext context = servletContextEvent.getServletContext();
    }


    @Override
    public void contextDestroyed( final ServletContextEvent servletContextEvent ) {
        super.contextDestroyed( servletContextEvent );

        if ( esEmbedded != null ) {
            esEmbedded.stop();
        }
    }
}

