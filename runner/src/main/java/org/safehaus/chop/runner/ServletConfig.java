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

import org.safehaus.chop.api.StoreService;
import org.safehaus.guicyfig.Env;
import org.safehaus.guicyfig.GuicyFigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.netflix.blitz4j.LoggingConfiguration;
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

        final ServletFig servletFig = Guice.createInjector(
                new GuicyFigModule( ServletFig.class ) ).getInstance( ServletFig.class );

        LoggingConfiguration.getInstance().configure();
        storeService = getInjector().getInstance( StoreService.class );

        ServletContext context = servletContextEvent.getServletContext();
        servletFig.override( ServletFig.CONTEXT_PATH, context.getContextPath() );
        servletFig.override( ServletFig.SERVER_INFO_KEY, context.getServerInfo() );
        servletFig.override( ServletFig.CONTEXT_TEMPDIR_KEY,
                ( ( File ) context.getAttribute( ServletFig.CONTEXT_TEMPDIR_KEY ) ).getAbsolutePath() );

        storeService.start();
    }


    @Override
    public void contextDestroyed( ServletContextEvent servletContextEvent ) {
        LoggingConfiguration.getInstance().stop();

        if ( storeService != null ) {
            storeService.stop();
        }
        super.contextDestroyed( servletContextEvent );
    }
}
