package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.client.IndicesAdminClient;
import org.reflections.Reflections;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;


public class SetupDao {

    private final static Logger LOG = LoggerFactory.getLogger( SetupDao.class );

    protected IElasticSearchClient elasticSearchClient;

    @Inject
    public SetupDao( IElasticSearchClient elasticSearchClient ) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public void setup() throws IOException, NoSuchFieldException, IllegalAccessException {

        String key;
        CreateIndexResponse ciResp;

        Reflections reflections = new Reflections( "org.safehaus.chop.webapp.dao" );
        Set<Class<? extends Dao>> daoClasses = reflections.getSubTypesOf( Dao.class );

        IndicesAdminClient client = elasticSearchClient.getClient().admin().indices();

        for( Class<? extends Dao> daoClass : daoClasses ) {

            key = daoClass.getDeclaredField( "DAO_INDEX_KEY" ).get( null ).toString();

            if( ! client.exists( new IndicesExistsRequest( key ) ).actionGet().isExists() ) {
                ciResp = client.create( new CreateIndexRequest( key ) ).actionGet();
                if( ciResp.isAcknowledged() ) {
                    LOG.debug( "Index for key {} didn't exist, now created", key );
                }
                else {
                    LOG.debug( "Could not create index for key: {}", key );
                }
            }
            else {
                LOG.debug( "Key {} already exists", key );
            }
        }
    }

}