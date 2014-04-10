package org.apache.usergrid.chop.webapp.dao;

import org.apache.usergrid.chop.webapp.elasticsearch.IElasticSearchClient;
import org.elasticsearch.action.search.SearchRequestBuilder;
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

    /**
     * By default ElasticSearch searches with lower-case and ignores the dash. We need this fix to get correct result.
     */
    protected static String fixTermValue(String value) {
        return value != null
            ? value.toLowerCase().replaceAll("-", "")
            : null;
    }
}


