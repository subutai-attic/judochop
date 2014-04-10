package org.apache.usergrid.chop.webapp.dao;

import com.google.inject.Inject;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
<<<<<<< HEAD:webapp/src/main/java/org/safehaus/chop/webapp/dao/ModuleDao.java
import org.apache.usergrid.chop.api.Module;
import org.safehaus.chop.webapp.dao.model.BasicModule;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;
=======
import org.safehaus.chop.api.Module;
import org.apache.usergrid.chop.webapp.dao.model.BasicModule;
import org.apache.usergrid.chop.webapp.elasticsearch.IElasticSearchClient;
import org.apache.usergrid.chop.webapp.elasticsearch.Util;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:webapp/src/main/java/org/apache/usergrid/chop/webapp/dao/ModuleDao.java

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class ModuleDao extends Dao {

    public static final String DAO_INDEX_KEY = "modules";
    public static final String DAO_TYPE_KEY = "module";

    private static final int MAX_RESULT_SIZE = 1000;


    @Inject
    public ModuleDao( IElasticSearchClient elasticSearchClient ) {
        super( elasticSearchClient );
    }


    public boolean save( Module module ) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( DAO_INDEX_KEY, DAO_TYPE_KEY, module.getId() )
                .setRefresh( true )
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field( "groupId", module.getGroupId() )
                                .field( "artifactId", module.getArtifactId() )
                                .field( "version", module.getVersion() )
                                .field( "vcsRepoUrl", module.getVcsRepoUrl() )
                                .field( "testPackageBase", module.getTestPackageBase() )
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }


    public Module get( String id ) {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( DAO_INDEX_KEY )
                .setTypes( DAO_TYPE_KEY )
                .setQuery( termQuery( "_id", id ) )
                .execute()
                .actionGet();

        SearchHit hits[] = response.getHits().hits();

        return hits.length > 0 ? toModule( hits[0] ) : null;
    }


    private static Module toModule( SearchHit hit ) {
        Map<String, Object> json = hit.getSource();

        return new BasicModule(
                Util.getString( json, "groupId" ),
                Util.getString( json, "artifactId" ),
                Util.getString( json, "version" ),
                Util.getString( json, "vcsRepoUrl" ),
                Util.getString( json, "testPackageBase" )
        );
    }

    public List<Module> getAll() {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( DAO_INDEX_KEY )
                .setTypes( DAO_TYPE_KEY )
                .setSize( MAX_RESULT_SIZE )
                .execute()
                .actionGet();

        ArrayList<Module> modules = new ArrayList<Module>();

        for ( SearchHit hit : response.getHits().hits() ) {
            modules.add( toModule( hit ) );
        }

        return modules;
    }
}