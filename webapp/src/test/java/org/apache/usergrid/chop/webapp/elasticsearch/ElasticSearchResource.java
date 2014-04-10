package org.apache.usergrid.chop.webapp.elasticsearch;


import java.io.File;

import org.apache.usergrid.chop.webapp.elasticsearch.ElasticSearchFig;
import org.apache.usergrid.chop.webapp.elasticsearch.EsEmbedded;
import org.elasticsearch.common.io.FileSystemUtils;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.safehaus.jettyjam.utils.StartableResource;


public class ElasticSearchResource implements StartableResource {
    private final EsEmbedded embedded = new EsEmbedded();


    @Override
    public void start( Description description ) throws Exception {
        FileSystemUtils.deleteRecursively( new File( embedded.getConfig().getDataDir() ) );
        embedded.start();
    }


    public ElasticSearchFig getConfig() {
        return embedded.getConfig();
    }


    @Override
    public void stop( Description description ) {
        embedded.stop();
        FileSystemUtils.deleteRecursively( new File( embedded.getConfig().getDataDir() ) );
    }


    @Override
    public boolean isStarted() {
        return embedded.isStarted();
    }


    @Override
    public Statement apply( final Statement base, final Description description ) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                start( description );
                try {
                    base.evaluate();
                }
                finally {
                    stop( description );
                }
            }
        };
    }
}
