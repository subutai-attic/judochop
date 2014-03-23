package org.safehaus.chop.webapp.elasticsearch;


import org.junit.rules.ExternalResource;


public class ElasticSearchResource extends ExternalResource {
    private final EsEmbedded embedded = new EsEmbedded();


    protected void before() throws Exception {
        embedded.start();
    }


    public ElasticSearchFig getConfig() {
        return embedded.getConfig();
    }


    protected void after() {
        embedded.stop();
    }
}
