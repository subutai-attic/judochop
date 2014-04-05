package org.safehaus.chop.webapp.elasticsearch;


import java.io.File;

import org.elasticsearch.common.io.FileSystemUtils;
import org.junit.rules.ExternalResource;


public class ElasticSearchResource extends ExternalResource {
    private final EsEmbedded embedded = new EsEmbedded();


    protected void before() throws Exception {
        FileSystemUtils.deleteRecursively( new File( embedded.getConfig().getDataDir() ) );
        embedded.start();
    }


    public ElasticSearchFig getConfig() {
        return embedded.getConfig();
    }


    protected void after() {
        embedded.stop();
        FileSystemUtils.deleteRecursively( new File( embedded.getConfig().getDataDir() ) );
    }
}
