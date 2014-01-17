/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/8/13
 * Time: 1:48 PM
 */
package org.safehaus.chop.client;


import org.safehaus.chop.api.Constants;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.store.amazon.AmazonStoreModule;
import org.safehaus.guicyfig.GuicyFigModule;

import com.google.inject.AbstractModule;


public class ChopClientModule extends AbstractModule implements Constants {

    protected void configure() {
        //noinspection unchecked
        install( new GuicyFigModule( Project.class ) );
        install( new AmazonStoreModule() );
        bind( ChopClient.class ).to( ChopClientImpl.class );
    }
}
