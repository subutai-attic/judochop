package org.safehaus.chop.webapp.coordinator.rest;


import org.safehaus.guicyfig.Default;
import org.safehaus.guicyfig.GuicyFig;
import org.safehaus.guicyfig.Key;


/**
 * Rest configuration GuicyFig bean.
 */
public interface RestFig extends GuicyFig {

    /**
     * Gets the base upload path to upload war files.
     */
    @Key( "war.upload.path" )
    @Default( "." )
    String getWarUploadPath();


}
