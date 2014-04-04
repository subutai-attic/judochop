package org.safehaus.chop.webapp.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtil {

    private final static Logger LOG = LoggerFactory.getLogger(TimeUtil.class);

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            LOG.error("Exception while thread sleep: ", e);
        }
    }

}
