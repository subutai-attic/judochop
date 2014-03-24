package org.safehaus.chop.webapp.dao;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Dao {

    protected final Logger LOG = LoggerFactory.getLogger( getClass() );

    protected IElasticSearchClient elasticSearchClient;


    protected Dao( IElasticSearchClient elasticSearchClient ) {
        this.elasticSearchClient = elasticSearchClient;
    }


    protected SearchRequestBuilder getRequest(String index, String type) {
        return elasticSearchClient.getClient()
                .prepareSearch( index )
                .setTypes( type );
    }
}


