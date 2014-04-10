/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/8/13
 * Time: 1:48 PM
 */
package org.usergrid.chop.client;


import org.apache.usergrid.chop.api.Constants;
import org.apache.usergrid.chop.api.Project;
import org.apache.usergrid.chop.api.store.amazon.AmazonModule;
import org.safehaus.guicyfig.GuicyFigModule;

import com.google.inject.AbstractModule;


public class ChopClientModule extends AbstractModule implements Constants {

    protected void configure() {
        //noinspection unchecked
        install( new GuicyFigModule( Project.class ) );
        install( new AmazonModule() );
    }
}
