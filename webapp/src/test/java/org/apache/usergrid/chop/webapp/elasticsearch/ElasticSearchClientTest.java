package org.apache.usergrid.chop.webapp.elasticsearch;

import com.google.inject.Inject;
import org.apache.usergrid.chop.webapp.elasticsearch.ElasticSearchClient;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.usergrid.chop.webapp.ChopUiModule;

import static junit.framework.TestCase.assertNotNull;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class ElasticSearchClientTest {

    @Inject
    ElasticSearchClient elasticSearchClient;

    @Test
    public void test() {
        assertNotNull(elasticSearchClient);
        assertNotNull(elasticSearchClient.getClient());
    }
}
