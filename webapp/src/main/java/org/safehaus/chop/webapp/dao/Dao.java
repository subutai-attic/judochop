package org.safehaus.chop.webapp.dao;

import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;


public abstract class Dao<T> {

    protected IElasticSearchClient elasticSearchClient;

    protected Dao( IElasticSearchClient elasticSearchClient ) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public abstract boolean save( T v ) throws Exception;

}


