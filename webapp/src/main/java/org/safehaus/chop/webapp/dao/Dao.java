package org.safehaus.chop.webapp.dao;

import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;

public abstract class Dao<T> {

    protected ElasticSearchClient elasticSearchClient;

    protected Dao(ElasticSearchClient elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public abstract boolean save(T v) throws Exception;

}


